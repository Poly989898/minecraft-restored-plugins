package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class TeleportWithListener implements Listener {
  private CMI plugin;
  HashMap<UUID, TeleportWithListener.vehicleLast> mapVehicle = new HashMap<>();

  public TeleportWithListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onTeleportWithEntity(PlayerTeleportEvent playerTeleportEvent) {
    if (!playerTeleportEvent.isCancelled()) {
      Player player = playerTeleportEvent.getPlayer();
      Entity entity = player.getVehicle();
      if (entity != null) {
        if (!Version.isCurrentEqual(Version.v1_16_R1)
            || !playerTeleportEvent.getCause().equals(TeleportCause.UNKNOWN)) {
          if (Version.isCurrentLower(Version.v1_20_R1) && !Version.isPaperBranch()) {
            this.mapVehicle.put(
                player.getUniqueId(),
                new TeleportWithListener.vehicleLast(player.getUniqueId(), entity, 0));
          } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
            this.mapVehicle.put(
                player.getUniqueId(),
                new TeleportWithListener.vehicleLast(
                    player.getUniqueId(),
                    entity,
                    CMILib.getInstance().getReflectionManager().getCurrentTick()));
          }

          if (playerTeleportEvent.getTo() != null) {
            if (!playerTeleportEvent
                    .getFrom()
                    .getWorld()
                    .equals(playerTeleportEvent.getTo().getWorld())
                || !(playerTeleportEvent.getFrom().distance(playerTeleportEvent.getTo()) <= 2.0)) {
              if (PermissionsManager.CMIPerm.teleport_with_$1.hasPermission(
                  player, entity.getType().name().toLowerCase())) {
                if (!entity.getType().equals(EntityType.ARMOR_STAND)
                    || PermissionsManager.CMIPerm.teleport_with_$1.hasSetPermission(
                        player, entity.getType().name().toLowerCase())) {
                  player.leaveVehicle();
                  if (!entity.isDead()) {
                    if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)
                        && Version.isPaperBranch()) {
                      this.plugin
                          .getTeleportHandler()
                          .teleportEntity(entity, playerTeleportEvent.getTo(), TeleportCause.PLUGIN)
                          .thenApply(
                              success -> {
                                if (!success) {
                                  return null;
                                }

                                if (!player.isOnline()) {
                                  return null;
                                }

                                CMIScheduler.runAtLocationLater(
                                    this.plugin,
                                    entity.getLocation(),
                                    () -> {
                                      if (player.getWorld().equals(entity.getWorld())
                                          && player.getLocation().distance(entity.getLocation())
                                              <= 4.0) {
                                        entity.addPassenger(player);
                                      }
                                    },
                                    20L);
                                return null;
                              });
                    } else {
                      entity.teleport(playerTeleportEvent.getTo());
                      CMIScheduler.runTask(
                          this.plugin,
                          () -> {
                            if (!player.isInsideVehicle()) {
                              TeleportWithListener.vehicleLast lastVehicle =
                                  this.mapVehicle.get(player.getUniqueId());
                              if (lastVehicle != null
                                  && (lastVehicle.getTick() <= 0
                                      || lastVehicle.getTick() + 30
                                          >= CMILib.getInstance()
                                              .getReflectionManager()
                                              .getCurrentTick())) {
                                Entity lastEntity = lastVehicle.getVehicle();
                                if (lastEntity != null) {
                                  if (!lastEntity.isDead()) {
                                    playerTeleportEvent.getTo().getChunk().load(true);
                                    lastEntity.teleport(playerTeleportEvent.getTo());
                                    CMIScheduler.runTaskLater(
                                        this.plugin,
                                        () -> {
                                          if (player.isOnline()) {
                                            TeleportWithListener.vehicleLast candidateVehicle =
                                                this.mapVehicle.get(player.getUniqueId());
                                            if (lastVehicle.getTick() > 0
                                                && candidateVehicle.getTick() + 30
                                                    < CMILib.getInstance()
                                                        .getReflectionManager()
                                                        .getCurrentTick()) {
                                              this.mapVehicle.remove(player.getUniqueId());
                                              return;
                                            }

                                            Entity candidateEntity = candidateVehicle.getVehicle();
                                            if (candidateEntity.isDead()) {
                                              return;
                                            }

                                            if (player.getWorld().equals(candidateEntity.getWorld())
                                                && player
                                                        .getLocation()
                                                        .distance(candidateEntity.getLocation())
                                                    <= 5.0) {
                                              if (Version.isCurrentLower(Version.v1_11_R1)) {
                                                candidateEntity.setPassenger(player);
                                              } else {
                                                candidateEntity.addPassenger(player);
                                              }
                                            }
                                          }
                                        },
                                        20L);
                                  }
                                }
                              } else {
                                this.mapVehicle.remove(player.getUniqueId());
                              }
                            }
                          });
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  class vehicleLast {
    private UUID uuid;
    private Entity vehicle;
    private int tick;

    public vehicleLast(UUID playerUuid, Entity entity, int value) {
      this.uuid = playerUuid;
      this.vehicle = entity;
      this.tick = value;
    }

    public UUID getUuid() {
      return this.uuid;
    }

    public Entity getVehicle() {
      return this.vehicle;
    }

    public int getTick() {
      return this.tick;
    }
  }
}
