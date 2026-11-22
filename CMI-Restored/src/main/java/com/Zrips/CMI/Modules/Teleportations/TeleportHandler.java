package com.Zrips.CMI.Modules.Teleportations;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIGod;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.events.CMIAsyncPlayerTeleportEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Items.CMIMC;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.PaperMethods.PaperLib;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public class TeleportHandler {
  private CMI plugin;
  private Set<CMIMaterial> emptyBlockMap = new HashSet<>();
  private Set<CMIMaterial> badBlockMap = new HashSet<>();
  private HashMap<UUID, DangerousTp> badTeleports = new HashMap<>();
  private int btprange = 15;
  static Method getBlockTypeId = null;
  boolean enabledDouble = true;

  public TeleportHandler(CMI cmi) {
    this.plugin = cmi;
    this.fill();
  }

  public void addBadTeleport(UUID uuid, Location location) {
    DangerousTp dangerousTp = new DangerousTp(uuid, location);
    this.badTeleports.put(uuid, dangerousTp);
  }

  public void removeBadTeleport(UUID uuid) {
    this.badTeleports.remove(uuid);
  }

  public Location getBadTeleport(UUID uuid) {
    DangerousTp dangerousTp = this.badTeleports.get(uuid);
    if (dangerousTp == null) {
      return null;
    } else if (dangerousTp.getTime() + this.btprange * 1000 < System.currentTimeMillis()) {
      this.badTeleports.remove(uuid);
      return null;
    } else {
      return dangerousTp.getLocation();
    }
  }

  public CompletableFuture<Material> getBlockType(Location location, boolean flag) {
    return this.getSnapshot(location, flag, false)
        .thenApplyAsync(
            cmiChunkSnapShot -> {
              if (cmiChunkSnapShot.getSnapshot() == null) {
                return Material.AIR;
              }

              int value = location.getBlockX() & 15;
              int localZ = location.getBlockZ() & 15;
              int localY = location.getBlockY();
              return cmiChunkSnapShot.getSnapshot().getHighestBlockYAt(value, localZ) < localY
                  ? Material.AIR
                  : cmiChunkSnapShot.getSnapshot().getBlockType(value, localY, localZ);
            });
  }

  public CompletableFuture<CMIChunkSnapShot> getSnapshot(
      Location location, boolean flag, boolean ignoreCheck) {
    return this.getSnapshot(
        location.getWorld(),
        location.getBlockX() >> 4,
        location.getBlockZ() >> 4,
        flag,
        ignoreCheck);
  }

  public CompletableFuture<CMIChunkSnapShot> getSnapshot(
      World world, int value, int chunkZ, boolean flag, boolean forceLoad) {
    if (world == null) {
      return CompletableFuture.completedFuture(null);
    }

    CompletableFuture<Chunk> completableFuture = null;

    try {
      if (!Version.isPaperBranch()) {
        return CompletableFuture.<CMIChunkSnapShot>supplyAsync(() -> new CMIChunkSnapShot(world))
            .thenCompose(
                cmiChunkSnapShot -> {
                  var chunkFuture =
                      CMIScheduler.runAtLocation(
                              this.plugin,
                              new Location(world, value * 16, 0.0, chunkZ * 16),
                              () -> {
                                try {
                                  cmiChunkSnapShot.setSnapshot(
                                      world
                                          .getChunkAt(value, chunkZ)
                                          .getChunkSnapshot(true, forceLoad, false));
                                } catch (Throwable throwable) {
                                  throwable.printStackTrace();
                                }
                              })
                          .exceptionally(
                              error -> {
                                error.printStackTrace();
                                return null;
                              });
                  chunkFuture =
                      chunkFuture.exceptionally(
                          object -> {
                            CMIMessages.consoleMessage(
                                "Could not get chunk snapshot for "
                                    + world
                                    + " "
                                    + value * 16
                                    + ":"
                                    + chunkZ * 16);
                            object.printStackTrace();
                            return null;
                          });
                  return chunkFuture.thenApply(snapshot -> cmiChunkSnapShot);
                })
            .exceptionally(
                chunkResult -> {
                  chunkResult.printStackTrace();
                  return null;
                });
      }

      completableFuture = PaperLib.getChunkAtAsync(world, value, chunkZ, flag);
    } catch (Throwable throwable3) {
      throwable3.printStackTrace();
      return CompletableFuture.completedFuture(null);
    }

    return completableFuture == null
        ? CompletableFuture.completedFuture(null)
        : completableFuture.thenComposeAsync(
            snapshotResult -> {
              CMIChunkSnapShot chunkSnapshot = new CMIChunkSnapShot(world);
              if (snapshotResult == null) {
                return CompletableFuture.completedFuture(chunkSnapshot);
              }

              CompletableFuture<CMIChunkSnapShot> resultFuture = new CompletableFuture<>();
              CMIScheduler.runAtLocation(
                  this.plugin,
                  new Location(world, value * 16, 0.0, chunkZ * 16),
                  () -> {
                    try {
                      chunkSnapshot.setSnapshot(
                          snapshotResult.getChunkSnapshot(true, forceLoad, false));
                      resultFuture.complete(chunkSnapshot);
                    } catch (Throwable throwable4) {
                      CMIMessages.consoleMessage(
                          "Unable to get chunk snapshot for "
                              + world
                              + " "
                              + value * 16
                              + ":"
                              + chunkZ * 16);
                      throwable4.printStackTrace();
                      resultFuture.complete(chunkSnapshot);
                    }
                  });
              return resultFuture;
            });
  }

  public CMIMaterial getBlockType(
      ChunkSnapshot chunkSnapshot, int value, PositionRelativeData positionRelativeData) {
    if (value > positionRelativeData.getMaxWorldY()
        || value < positionRelativeData.getMinWorldY()
        || chunkSnapshot == null) {
      return CMIMaterial.AIR;
    }

    if (!Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      if (chunkSnapshot.getHighestBlockYAt(
              positionRelativeData.getLocalX(), positionRelativeData.getLocalZ())
          < value) {
        return CMIMaterial.AIR;
      }

      if (getBlockTypeId == null) {
        try {
          getBlockTypeId =
              chunkSnapshot.getClass().getMethod("getBlockTypeId", int.class, int.class, int.class);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }

      try {
        return CMIMaterial.get(
            (Integer)
                getBlockTypeId.invoke(
                    chunkSnapshot,
                    positionRelativeData.getLocalX(),
                    value,
                    positionRelativeData.getLocalZ()));
      } catch (Throwable throwable2) {
        throwable2.printStackTrace();
        return CMIMaterial.AIR;
      }
    } else {
      return value >= positionRelativeData.getMaxWorldY()
          ? CMIMaterial.AIR
          : CMIMaterial.get(
              chunkSnapshot.getBlockType(
                  positionRelativeData.getLocalX(), value, positionRelativeData.getLocalZ()));
    }
  }

  public CompletableFuture<CMITeleportLocation> getSafeTeleportationLocation(
      CMITeleportationBounds cmiTeleportationBounds,
      CompletableFuture<CMIChunkSnapShot> completableFuture) {
    return completableFuture
        .<CMITeleportLocation>thenApplyAsync(
            cmiChunkSnapShot -> {
              CMITeleportLocation cmiTeleportLocation =
                  new CMITeleportLocation(cmiChunkSnapShot.getWorld());
              cmiTeleportLocation.setYaw(cmiTeleportationBounds.getYaw());
              cmiTeleportLocation.setPitch(cmiTeleportationBounds.getPitch());
              if (cmiChunkSnapShot.getWorld() == null) {
                return cmiTeleportLocation.setCondition(CMITeleportCondition.MissingWorld);
              }

              ChunkSnapshot chunkSnapshot = cmiChunkSnapShot.getSnapshot();
              PositionRelativeData positionRelativeData =
                  new PositionRelativeData(
                      new Location(
                          cmiChunkSnapShot.getWorld(),
                          cmiTeleportationBounds.getPosition().getX(),
                          64.0,
                          cmiTeleportationBounds.getPosition().getZ()));
              int value = -999;
              double number = -1.0;
              if (cmiTeleportationBounds.getWorld().getEnvironment() == Environment.NETHER
                  && cmiTeleportationBounds.getPosition().getY() <= 128.0) {
                cmiTeleportationBounds.setMaxY(
                    CMI.getInstance()
                            .getUtilManager()
                            .getMaxWorldHeight(cmiTeleportationBounds.getWorld())
                        - 3);
              }

              try {
                CMIHitBox cmiHitBox = new CMIHitBox(cmiTeleportationBounds.getPlayer());
                cmiHitBox.setSnapshot(chunkSnapshot);
                if (!Version.isFolia()) {
                  if (cmiHitBox.isEmptySpace(
                      cmiTeleportationBounds
                          .getPosition()
                          .toLocation(cmiTeleportationBounds.getWorld())
                          .add(0.0, 0.01, 0.0))) {
                    double scale = 1.0;
                    double offsetY =
                        cmiTeleportationBounds.getPosition().getY()
                            - (int) cmiTeleportationBounds.getPosition().getY();
                    if (offsetY > 0.0) {
                      scale = offsetY / 2.0;
                      CMIMaterial cmiMaterial =
                          this.getBlockType(
                              chunkSnapshot,
                              (int) (cmiTeleportationBounds.getPosition().getY() - scale),
                              positionRelativeData);
                      if (!this.isEmpty(cmiMaterial, false)) {
                        value = (int) cmiTeleportationBounds.getPosition().getY();
                        number = offsetY;
                      }
                    }

                    if (value == -999) {
                      CMIMaterial materialAbove =
                          this.getBlockType(
                              chunkSnapshot,
                              (int) cmiTeleportationBounds.getPosition().getY() - 1,
                              positionRelativeData);
                      if (!this.isEmpty(materialAbove, false)) {
                        value = (int) cmiTeleportationBounds.getPosition().getY();
                      }
                    }
                  }

                  try {
                    number =
                        cmiHitBox.getHeightAtLocation(
                            cmiTeleportationBounds
                                .getPosition()
                                .toLocation(cmiTeleportationBounds.getWorld()));
                  } catch (Throwable throwable) {
                    throwable.printStackTrace();
                  }

                  if (value == -999
                      && cmiHitBox.isEmptySpace(
                          cmiTeleportationBounds
                              .getPosition()
                              .toLocation(cmiTeleportationBounds.getWorld())
                              .add(0.0, 1.01, 0.0))) {
                    CMIMaterial materialBelow =
                        this.getBlockType(
                            chunkSnapshot,
                            (int) cmiTeleportationBounds.getPosition().getY(),
                            positionRelativeData);
                    if (!this.isEmpty(materialBelow, false)) {
                      value = (int) cmiTeleportationBounds.getPosition().getY() + 1;

                      try {
                        if (!Version.isFolia()) {
                          number =
                              cmiHitBox.getHeightAtLocation(
                                  cmiTeleportationBounds
                                      .getPosition()
                                      .toLocation(cmiTeleportationBounds.getWorld())
                                      .clone()
                                      .add(0.0, 1.0, 0.0));
                        }
                      } catch (Throwable throwable2) {
                        throwable2.printStackTrace();
                      }
                    }
                  }
                }
              } catch (Throwable throwable3) {
                throwable3.printStackTrace();
              }

              if (value == -999) {
                if (!this.isEmptySpaceForPlayer(
                    chunkSnapshot,
                    (int) cmiTeleportationBounds.getPosition().getY(),
                    positionRelativeData)) {
                  for (int scanY = (int) cmiTeleportationBounds.getPosition().getY();
                      scanY <= cmiTeleportationBounds.getMaxY() + 2;
                      scanY++) {
                    if (!this.isEmptySpaceForPlayer(chunkSnapshot, scanY, positionRelativeData)) {
                      if (cmiTeleportationBounds.isImortal()) {
                        value = scanY;
                      }
                    } else {
                      CMIMaterial materialCheck =
                          this.getBlockType(chunkSnapshot, scanY - 1, positionRelativeData);
                      if (!materialCheck.isAir()) {
                        value = scanY;
                        break;
                      }
                    }
                  }
                } else if (cmiTeleportationBounds.isCanFly()) {
                  value = (int) cmiTeleportationBounds.getPosition().getY();
                } else {
                  for (int scanYDown = (int) cmiTeleportationBounds.getPosition().getY();
                      scanYDown >= cmiTeleportationBounds.getMinY();
                      value = scanYDown--) {
                    if (!this.isEmptySpaceForPlayer(
                        chunkSnapshot, scanYDown, positionRelativeData)) {
                      if (cmiTeleportationBounds.isImortal()) {
                        value = scanYDown + 1;
                      }
                      break;
                    }
                  }
                }
              }

              if (value == -999 && cmiTeleportationBounds.isImortal()) {
                value = (int) cmiTeleportationBounds.getPosition().getY();
              }

              if (value > -999) {
                double bestDistance = -1.0;
                int minHeight = CMIWorld.getMinHeight(cmiTeleportationBounds.getWorld());
                CMIMaterial candidateMaterial = null;

                try {
                  for (int scanIndex = value; scanIndex >= minHeight; scanIndex--) {
                    candidateMaterial =
                        scanIndex >= positionRelativeData.getMaxWorldY()
                            ? CMIMaterial.AIR
                            : this.getBlockType(chunkSnapshot, scanIndex, positionRelativeData);
                    if (!this.isEmpty(candidateMaterial, false)) {
                      break;
                    }

                    bestDistance++;
                  }
                } catch (Throwable throwable4) {
                  throwable4.printStackTrace();
                }

                cmiTeleportLocation.setFloor(candidateMaterial);
                if (!cmiTeleportationBounds.isImortal() && bestDistance > 5.0) {
                  if (value - bestDistance <= minHeight) {
                    cmiTeleportLocation.setCondition(CMITeleportCondition.Void);
                  } else if (candidateMaterial != null
                      && !candidateMaterial.isWater()
                      && !candidateMaterial.equals(CMIMaterial.SLIME_BLOCK)) {
                    cmiTeleportLocation.setCondition(CMITeleportCondition.Fall);
                  }
                }

                double distance =
                    cmiTeleportationBounds.getPosition().getY()
                        - (int) cmiTeleportationBounds.getPosition().getY();
                cmiTeleportLocation.setFallDistance(bestDistance + distance);
                if (!cmiTeleportationBounds.isImortal() && this.isBad(candidateMaterial)) {
                  cmiTeleportLocation.setCondition(CMITeleportCondition.BadFloor);
                }

                if (number < 0.0
                    && (!this.getBlockType(chunkSnapshot, value, positionRelativeData).isAir()
                        || candidateMaterial != null
                            && (candidateMaterial.isFence()
                                || candidateMaterial.isGate()
                                || candidateMaterial.isWall()))) {
                  number = 0.501;
                }

                number = Math.max(number, 0.0);
                if ((long) cmiTeleportationBounds.getPosition().getY() == value && number == 0.0) {
                  number =
                      cmiTeleportationBounds.getPosition().getY()
                          - (long) cmiTeleportationBounds.getPosition().getY();
                }

                return cmiTeleportLocation.setPosition(
                    new Vector(
                        cmiTeleportationBounds.getPosition().getX(),
                        value + number,
                        cmiTeleportationBounds.getPosition().getZ()));
              } else {
                cmiTeleportLocation.setCondition(CMITeleportCondition.Suffocation);
                return cmiTeleportLocation;
              }
            })
        .exceptionally(
            error -> {
              error.printStackTrace();
              CMITeleportLocation resultLocation =
                  new CMITeleportLocation(cmiTeleportationBounds.getWorld());
              resultLocation.setYaw(cmiTeleportationBounds.getYaw());
              resultLocation.setPitch(cmiTeleportationBounds.getPitch());
              resultLocation.setCondition(CMITeleportCondition.Unknown);
              return resultLocation;
            });
  }

  public CompletableFuture<Boolean> teleportEntity(
      Entity entity, Location location, TeleportCause teleportCause) {
    return PaperLib.teleportAsync(entity, location, teleportCause);
  }

  public CompletableFuture<CMITeleportFeedback> teleportPlayer(
      Player player, Location location, boolean flag) {
    return this.teleportPlayer(player, location, TeleportCause.PLUGIN, flag, null, false);
  }

  public CompletableFuture<CMITeleportFeedback> teleportPlayer(Player player, Location location) {
    return this.teleportPlayer(
        player, location, TeleportCause.PLUGIN, hasSafeTeleport(player), null, false);
  }

  public CompletableFuture<CMITeleportFeedback> teleportPlayer(
      Player player, Location location, CMITeleportType cmiTeleportType) {
    return this.teleportPlayer(
        player, location, TeleportCause.PLUGIN, hasSafeTeleport(player), cmiTeleportType, false);
  }

  public CompletableFuture<CMITeleportFeedback> teleportPlayer(
      Player player, Location location, boolean flag, CMITeleportType cmiTeleportType) {
    return this.teleportPlayer(
        player, location, TeleportCause.PLUGIN, flag, cmiTeleportType, false);
  }

  public CompletableFuture<CMITeleportFeedback> teleportPlayer(
      Player player,
      Location location,
      boolean flag,
      CMITeleportType cmiTeleportType,
      boolean safeOnly) {
    return this.teleportPlayer(
        player, location, TeleportCause.PLUGIN, flag, cmiTeleportType, safeOnly);
  }

  public CompletableFuture<CMITeleportFeedback> teleportPlayer(
      Player player, Location location, TeleportCause teleportCause) {
    return this.teleportPlayer(
        player, location, teleportCause, hasSafeTeleport(player), null, false);
  }

  public CompletableFuture<CMITeleportFeedback> teleportPlayer(
      Player player, Location location, TeleportCause teleportCause, boolean flag) {
    return this.teleportPlayer(player, location, teleportCause, flag, null, false);
  }

  public CompletableFuture<Boolean> finalizeTeleportation(
      Player player, TeleportCause teleportCause, CMITeleportLocation cmiTeleportLocation) {
    SafeTeleport safeTeleport =
        new SafeTeleport(
            cmiTeleportLocation.getLocation(),
            cmiTeleportLocation.getLocation(),
            cmiTeleportLocation.getCondition());
    CMIAsyncPlayerTeleportEvent cmiAsyncPlayerTeleportEvent =
        new CMIAsyncPlayerTeleportEvent(
            player, player, safeTeleport, cmiTeleportLocation.getType());
    Bukkit.getServer().getPluginManager().callEvent(cmiAsyncPlayerTeleportEvent);
    if (!cmiAsyncPlayerTeleportEvent.isCancelled()
        && cmiTeleportLocation.getLocation() != null
        && cmiTeleportLocation.getLocation().getWorld() != null) {
      if (this.setTeleportInvulnerability(player, cmiTeleportLocation.getWorld())
          && cmiTeleportLocation.getCondition().equals(CMITeleportCondition.Fall)) {
        cmiTeleportLocation.setCondition(CMITeleportCondition.Good);
      }

      if (cmiTeleportLocation.getPosition() == null) {
        return CompletableFuture.completedFuture(false);
      }

      if (!player.isOnline()) {
        this.plugin.getNMS().setMiscLocation(player, cmiTeleportLocation.getLocation());
        CMIUser.getUser(player).unloadData();
        return CompletableFuture.completedFuture(true);
      }

      if (player.getPassenger() != null) {
        CompletableFuture completableFuture = new CompletableFuture();
        CMIScheduler.runAtLocation(
            this.plugin,
            player.getLocation(),
            () ->
                this.plugin
                    .getAnimationManager()
                    .removePassengers(player, null)
                    .thenCompose(
                        list ->
                            PaperLib.teleportAsync(
                                player, cmiTeleportLocation.getLocation(), teleportCause))
                    .whenComplete(
                        (teleported, throwable) -> {
                          if (throwable != null) {
                            throwable.printStackTrace();
                            completableFuture.complete(false);
                          } else {
                            completableFuture.complete(teleported);
                          }
                        }));
        return completableFuture;
      }

      if (Version.isFolia()) {
        Location location = player.getLocation();

        try {
          CompletableFuture resultFuture = new CompletableFuture();
          CMIScheduler.runAtEntity(
              this.plugin,
              player,
              () -> {
                try {
                  player
                      .teleportAsync(cmiTeleportLocation.getLocation(), teleportCause)
                      .whenComplete(
                          (teleported, teleportError) -> {
                            if (teleportError != null) {
                              resultFuture.completeExceptionally(teleportError);
                            } else {
                              CMIUser.getUser(player).setLastTeleportLocation(location, false);
                              resultFuture.complete(teleported);
                            }
                          });
                } catch (Throwable throwable3) {
                  resultFuture.completeExceptionally(throwable3);
                }
              });
        } catch (Throwable throwable4) {
          throwable4.printStackTrace();
          return CompletableFuture.completedFuture(false);
        }
      }

      return PaperLib.teleportAsync(player, cmiTeleportLocation.getLocation(), teleportCause)
          .exceptionally(
              error -> {
                error.printStackTrace();
                return false;
              });
    } else {
      return CompletableFuture.completedFuture(false);
    }
  }

  public CompletableFuture<CMITeleportFeedback> teleportPlayer(
      Player player,
      Location location,
      TeleportCause teleportCause,
      boolean flag,
      CMITeleportType cmiTeleportType) {
    return this.teleportPlayer(player, location, teleportCause, flag, cmiTeleportType, false);
  }

  public CompletableFuture<CMITeleportFeedback> teleportPlayer(
      Player player,
      Location location,
      TeleportCause teleportCause,
      boolean flag,
      CMITeleportType cmiTeleportType,
      boolean silent) {
    try {
      if (location.getWorld() != null) {
        location.setWorld(Bukkit.getServer().getWorld(location.getWorld().getUID()));
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    location.setPitch((float) CMINumber.clamp(location.getPitch(), -90.0, 90));
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R6) && player.isOnline()) {
      CMIUser.closeInventory(player);
    }

    Location playerLocation = player.getLocation();
    CMITeleportFeedback cmiTeleportFeedback = new CMITeleportFeedback(location, true);
    return this.getSnapshot(location, true, true)
        .thenComposeAsync(
            cmiChunkSnapShot -> {
              CompletableFuture<CMITeleportLocation> completableFuture;
              if (flag) {
                CMITeleportationBounds cmiTeleportationBounds =
                    new CMITeleportationBounds(location);
                cmiTeleportationBounds.setUpRange(0.0);
                cmiTeleportationBounds.setMaxY(
                    Math.max(cmiTeleportationBounds.getMaxY(), location.getBlockY()));
                cmiTeleportationBounds.setMinY(CMIWorld.getMinHeight(location.getWorld()) + 1);
                cmiTeleportationBounds.setImortal(isImortal(player));
                cmiTeleportationBounds.setCanFly(player.getAllowFlight());
                cmiTeleportationBounds.setFlying(player.isFlying());
                cmiTeleportationBounds.setPlayer(player);
                cmiTeleportationBounds.setIgnoreYLimits(silent);
                completableFuture =
                    this.getSafeTeleportationLocation(
                        cmiTeleportationBounds,
                        CompletableFuture.completedFuture(cmiChunkSnapShot));
              } else {
                completableFuture =
                    CompletableFuture.completedFuture(
                        new CMITeleportLocation(location.getWorld())
                            .setPosition(location.toVector())
                            .setPitch(location.getPitch())
                            .setYaw(location.getYaw()));
              }

              return completableFuture;
            })
        .thenApplyAsync(
            object -> {
              if (object == null) {
                cmiTeleportFeedback.setSuccess(false);
                return cmiTeleportFeedback;
              }

              cmiTeleportFeedback.setCondition(object.getCondition());
              cmiTeleportFeedback.setFloor(object.getFloor());
              cmiTeleportFeedback.setFallDistance(object.getFallDistance());
              cmiTeleportFeedback.setLocation(object.getLocation());
              object.setType(cmiTeleportType);

              try {
                if (flag && cmiTeleportFeedback.getCondition() != CMITeleportCondition.Good) {
                  if ((cmiTeleportFeedback.getCondition() == CMITeleportCondition.Fall
                          || cmiTeleportFeedback.getCondition() == CMITeleportCondition.Void
                          || cmiTeleportFeedback.getCondition() == CMITeleportCondition.BadFloor)
                      && player.isFlying()
                      && player.getWorld().equals(location.getWorld())
                      && cmiTeleportFeedback.getFallDistance() >= 1.0) {
                    cmiTeleportFeedback.setCondition(CMITeleportCondition.Good);
                  } else if (this.plugin
                                  .getTeleportManager()
                                  .getInvulnerabilityTime(object.getWorld())
                              >= 0
                          && cmiTeleportFeedback.getCondition() == CMITeleportCondition.Fall
                      || !cmiTeleportFeedback.getCondition().equals(CMITeleportCondition.Good)
                          && this.bypassesBadLocation(player, cmiTeleportFeedback.getCondition())) {
                    cmiTeleportFeedback.setCondition(CMITeleportCondition.Good);
                  }
                }
              } catch (Throwable throwable2) {
                throwable2.printStackTrace();
              }

              this.finalizeTeleportation(player, teleportCause, object)
                  .thenAccept(value -> cmiTeleportFeedback.setSuccess(value));
              return cmiTeleportFeedback;
            })
        .whenComplete(
            (feedback, error) -> {
              if (error != null) {
                error.printStackTrace();
                feedback.setSuccess(false);
              } else if (feedback.isSuccess()) {
                this.showParticleEffect(
                    player, cmiTeleportType, playerLocation, feedback.getLocation());
                CMIScheduler.runAtLocation(
                    this.plugin,
                    player.getLocation(),
                    () -> {
                      if (!Version.isTestServer()) {
                        if (feedback.getFallDistance() < 0.5
                            && !this.isBad(feedback.getFloor())
                            && player.isFlying()) {
                          player.setFlying(false);
                        } else if (feedback.getFallDistance() > 1.0 && player.getAllowFlight()) {
                          player.setFlying(true);
                        }
                      }
                    });
              }
            });
  }

  public void showParticleEffect(
      Player player, CMITeleportType cmiTeleportType, Location location, Location destination) {
    if (cmiTeleportType != null) {
      CMIUser cmiUser = CMIUser.getUser(player);
      if (cmiUser != null && !cmiUser.isVanished()) {
        try {
          if (cmiTeleportType.getFrom() != null) {
            cmiTeleportType.getFrom().show(location);
          }

          if (cmiTeleportType.getTo() != null) {
            cmiTeleportType.getTo().show(destination);
          }
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
    }
  }

  public List<Entity> shakePassengers(Entity entity) {
    ArrayList<Entity> arrayList = new ArrayList();
    if (Version.isCurrentEqualOrHigher(Version.v1_11_R1)) {
      for (Entity passenger : entity.getPassengers()) {
        if (passenger != null) {
          arrayList.add(passenger);
          passenger.leaveVehicle();
          if (passenger instanceof Slime
              && passenger.getCustomName().equalsIgnoreCase("CMIFakeSlime")) {
            passenger.remove();
          }

          return arrayList;
        }
      }
    }

    if (entity.getPassenger() == null) {
      return arrayList;
    }

    arrayList.add(entity.getPassenger());
    entity.getPassenger().leaveVehicle();
    if (entity.getPassenger() instanceof Slime
        && entity.getPassenger().getCustomName().equalsIgnoreCase("CMIFakeSlime")) {
      entity.getPassenger().remove();
    }

    return arrayList;
  }

  public boolean isEmptySpaceForPlayer(
      ChunkSnapshot chunkSnapshot, int value, PositionRelativeData positionRelativeData) {
    return this.isEmptySpaceForPlayer(chunkSnapshot, value, positionRelativeData, true);
  }

  public boolean isEmptySpaceForPlayer(
      ChunkSnapshot chunkSnapshot,
      int value,
      PositionRelativeData positionRelativeData,
      boolean flag) {
    if (value <= positionRelativeData.getMaxWorldY()
        && value >= positionRelativeData.getMinWorldY()
        && !this.isEmpty(this.getBlockType(chunkSnapshot, value, positionRelativeData), true)) {
      return false;
    } else if (value > positionRelativeData.getMaxWorldY()
        && !this.isEmpty(this.getBlockType(chunkSnapshot, value - 1, positionRelativeData), true)) {
      return false;
    } else {
      return !flag && Version.isCurrentEqualOrHigher(Version.v1_14_R1)
          ? true
          : value >= positionRelativeData.getMaxWorldY()
              || value < positionRelativeData.getMinWorldY()
              || this.isEmpty(
                  this.getBlockType(chunkSnapshot, value + 1, positionRelativeData), true);
    }
  }

  public boolean isEmpty(
      ChunkSnapshot chunkSnapshot, int value, PositionRelativeData positionRelativeData) {
    return value >= positionRelativeData.getMaxWorldY() - 1
        || value < positionRelativeData.getMinWorldY()
        || this.isEmpty(this.getBlockType(chunkSnapshot, value, positionRelativeData), true);
  }

  public boolean isEmpty(CMIMaterial cmiMaterial, boolean flag) {
    if (cmiMaterial == null) {
      return false;
    } else if (cmiMaterial.isNone() || cmiMaterial.isAir()) {
      return true;
    } else if (cmiMaterial.isLava()) {
      return false;
    } else {
      return flag && cmiMaterial.isWater() ? true : this.emptyBlockMap.contains(cmiMaterial);
    }
  }

  public boolean isBad(CMIMaterial cmiMaterial) {
    return this.badBlockMap.contains(cmiMaterial);
  }

  private boolean setTeleportInvulnerability(Player player, World world) {
    int value = this.plugin.getTeleportManager().getInvulnerabilityTime(world);
    if (player.isOnline() && value > 0) {
      CMIUser cmiUser = CMIUser.getUser(player);
      boolean flag = cmiUser.getTgod() > System.currentTimeMillis();
      if (!flag) {
        CMIGod.applyNoDamageTick(player, value * 20);
      }

      cmiUser.setTeleportInvulnerability(System.currentTimeMillis() + 1000L * value);
      InvulnerabilityListener.addImmortal(cmiUser, world);
      return true;
    } else {
      return false;
    }
  }

  public boolean needToFly(Player player) {
    int value = 0;
    Location location = player.getLocation().clone();
    int highY = location.getBlockY();
    int minHeight = CMIWorld.getMinHeight(location.getWorld());

    while (this.isEmpty(CMIMaterial.get(location.getBlock()), true) && highY > minHeight) {
      value++;
      location.setY(--highY);
    }

    return this.isBad(CMIMaterial.get(location.getBlock())) || highY <= minHeight
        ? true
        : value > 4;
  }

  public CompletableFuture<Double> getDistanceToDrop(Location location) {
    return this.getSnapshot(location, true, true)
        .thenApplyAsync(
            cmiChunkSnapShot -> {
              PositionRelativeData positionRelativeData = new PositionRelativeData(location);
              int value = 0;

              for (int startY = location.getBlockY();
                  startY >= CMIWorld.getMinHeight(location.getWorld())
                      && this.isEmpty(cmiChunkSnapShot.getSnapshot(), startY, positionRelativeData);
                  startY--) {
                value++;
              }

              return (double) value;
            });
  }

  public CompletableFuture<CMITeleportFeedback> getSafeLocation(
      Player player, Location location, int value, int maxHeight) {
    return this.getSafeLocation(player, location, value, maxHeight, true);
  }

  public CompletableFuture<CMITeleportFeedback> getSafeLocation(
      Player player, Location location, int value, int maxHeight, boolean flag) {
    CompletableFuture completableFuture = new CompletableFuture();
    this.getSnapshot(location, true, true)
        .thenComposeAsync(
            cmiChunkSnapShot -> {
              CMITeleportationBounds cmiTeleportationBounds = new CMITeleportationBounds(location);
              cmiTeleportationBounds.setUpRange(value);
              cmiTeleportationBounds.setDownRange(maxHeight);
              if (flag) {
                cmiTeleportationBounds.setImortal(isImortal(player));
                cmiTeleportationBounds.setCanFly(player.getAllowFlight());
                cmiTeleportationBounds.setFlying(player.isFlying());
              }

              return this.getSafeTeleportationLocation(
                  cmiTeleportationBounds, CompletableFuture.completedFuture(cmiChunkSnapShot));
            })
        .thenApplyAsync(
            cmiTeleportLocation -> {
              CMITeleportFeedback cmiTeleportFeedback = new CMITeleportFeedback(location, false);
              cmiTeleportFeedback.setCondition(cmiTeleportLocation.getCondition());
              if (cmiTeleportLocation.getPosition() == null) {
                return cmiTeleportFeedback;
              }

              cmiTeleportFeedback.setLocation(cmiTeleportLocation.getLocation());
              cmiTeleportFeedback.setFallDistance(cmiTeleportLocation.getFallDistance());
              cmiTeleportFeedback.setFloor(cmiTeleportLocation.getFloor());
              if ((cmiTeleportFeedback.getCondition() == CMITeleportCondition.Fall
                      || cmiTeleportFeedback.getCondition() == CMITeleportCondition.Void
                      || cmiTeleportFeedback.getCondition() == CMITeleportCondition.BadFloor)
                  && player.isFlying()
                  && player.getWorld().equals(location.getWorld())
                  && cmiTeleportFeedback.getFallDistance() >= 1.0) {
                cmiTeleportFeedback.setCondition(CMITeleportCondition.Good);
              }

              return cmiTeleportFeedback;
            })
        .whenComplete(
            (resultFeedback, throwable) -> {
              if (throwable != null) {
                throwable.printStackTrace();
                completableFuture.complete(new CMITeleportFeedback(location, false));
              } else {
                completableFuture.complete(resultFeedback);
              }
            });
    return completableFuture;
  }

  private static boolean isImortal(Player player) {
    CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
    return cmiUser == null
        ? false
        : cmiUser.isGod()
            || !player.getGameMode().equals(GameMode.SURVIVAL)
                && !player.getGameMode().equals(GameMode.ADVENTURE);
  }

  public static boolean hasSafeTeleport(Player player) {
    return player != null && player.isOnline()
        ? PermissionsManager.CMIPerm.safeteleport.hasPermission(player)
        : false;
  }

  public boolean bypassesBadLocation(Player player, CMITeleportCondition cmiTeleportCondition) {
    return PermissionsManager.CMIPerm.safeteleport_bypass_$1.hasPermission(
        player, cmiTeleportCondition.name());
  }

  public CompletableFuture<Location> getTopLocation(Location location) {
    return location == null
        ? CompletableFuture.completedFuture(null)
        : this.getSnapshot(location, true, true)
            .thenApplyAsync(
                cmiChunkSnapShot -> {
                  if (cmiChunkSnapShot.getWorld() == null) {
                    return null;
                  }

                  ChunkSnapshot chunkSnapshot = cmiChunkSnapShot.getSnapshot();
                  PositionRelativeData positionRelativeData = new PositionRelativeData(location);
                  int value =
                      location.getWorld().getEnvironment().equals(Environment.NETHER)
                          ? CMINumber.clamp(positionRelativeData.getMaxWorldY() - 3, 0, 125)
                          : positionRelativeData.getMaxWorldY();

                  try {
                    for (int index = value; index > positionRelativeData.getMinWorldY(); index--) {
                      if (this.isValidPosition(chunkSnapshot, index, positionRelativeData)) {
                        return new Location(
                            cmiChunkSnapShot.getWorld(),
                            location.getX(),
                            index + 1,
                            location.getZ());
                      }
                    }
                  } catch (Throwable throwable) {
                    throwable.printStackTrace();
                  }

                  return new Location(
                      cmiChunkSnapShot.getWorld(),
                      location.getX(),
                      chunkSnapshot.getHighestBlockYAt(
                          positionRelativeData.getLocalX(), positionRelativeData.getLocalZ()),
                      location.getZ());
                });
  }

  public CompletableFuture<Location> getDownLocation(Location location) {
    return location == null
        ? CompletableFuture.completedFuture(null)
        : this.getSnapshot(location, true, true)
            .thenApplyAsync(
                cmiChunkSnapShot -> {
                  if (cmiChunkSnapShot.getWorld() == null) {
                    return null;
                  }

                  ChunkSnapshot chunkSnapshot = cmiChunkSnapShot.getSnapshot();
                  PositionRelativeData positionRelativeData = new PositionRelativeData(location);

                  for (int value = location.getBlockY() - 2;
                      value >= positionRelativeData.getMinWorldY();
                      value--) {
                    if (this.isValidPosition(chunkSnapshot, value, positionRelativeData)) {
                      return new Location(
                          cmiChunkSnapShot.getWorld(), location.getX(), value + 1, location.getZ());
                    }
                  }

                  return null;
                });
  }

  private boolean isValidPosition(
      ChunkSnapshot chunkSnapshot, int value, PositionRelativeData positionRelativeData) {
    CMIMaterial cmiMaterial = this.getBlockType(chunkSnapshot, value, positionRelativeData);
    return !cmiMaterial.isAir() && !this.isEmpty(cmiMaterial, false)
        ? this.isEmptySpaceForPlayer(chunkSnapshot, value + 1, positionRelativeData)
        : false;
  }

  public CompletableFuture<Location> getBottomLocation(Location location) {
    return location == null
        ? CompletableFuture.completedFuture(null)
        : this.getSnapshot(location, true, true)
            .thenApplyAsync(
                cmiChunkSnapShot -> {
                  if (cmiChunkSnapShot.getWorld() == null) {
                    return null;
                  }

                  ChunkSnapshot chunkSnapshot = cmiChunkSnapShot.getSnapshot();
                  PositionRelativeData positionRelativeData = new PositionRelativeData(location);

                  for (int value = positionRelativeData.getMinWorldY();
                      value < location.getBlockY();
                      value++) {
                    if (this.isValidPosition(chunkSnapshot, value, positionRelativeData)) {
                      return new Location(
                          cmiChunkSnapShot.getWorld(), location.getX(), value + 1, location.getZ());
                    }
                  }

                  return null;
                });
  }

  private void fill() {
    this.emptyBlockMap.clear();
    CMIMaterial[] cmiMaterials;
    int value = (cmiMaterials = CMIMaterial.values()).length;

    for (int index = 0; index < value; index++) {
      CMIMaterial cmiMaterial = cmiMaterials[index];
      if (cmiMaterial.containsCriteria(new CMIMC[] {CMIMC.CARPET, CMIMC.NOCOLLISIONBOX})) {
        this.emptyBlockMap.add(cmiMaterial);
      }
    }

    this.badBlockMap.add(CMIMaterial.LAVA);
    this.badBlockMap.add(CMIMaterial.FIRE);
    if (!Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      this.badBlockMap.add(CMIMaterial.LEGACY_STATIONARY_LAVA);
    }

    this.badBlockMap.add(CMIMaterial.MAGMA_BLOCK);
    this.badBlockMap.add(CMIMaterial.CACTUS);
  }
}
