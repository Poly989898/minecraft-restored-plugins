package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIWorldLimits;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.list.alert;
import com.Zrips.CMI.commands.list.lastonline;
import com.Zrips.CMI.commands.list.lockip;
import com.Zrips.CMI.commands.list.mail;
import com.Zrips.CMI.commands.list.patrol;
import com.Zrips.CMI.events.CMIUserBalanceChangeEvent;
import com.Zrips.CMI.utils.CMINPCChecker;
import com.Zrips.CMI.utils.SpawnUtil;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

public class UserEvents implements Listener {
  private CMI plugin;
  private static HashMap<UUID, Long> backExclusionMap = new HashMap<>();
  HashMap<UUID, Long> roofTeleporterDelay = new HashMap<>();
  HashMap<UUID, Long> informer = new HashMap<>();

  public UserEvents(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onCMIUserBalanceChangeEvent(CMIUserBalanceChangeEvent cmiUserBalanceChangeEvent) {
    this.plugin.getEconomyManager().updateBalTop(cmiUserBalanceChangeEvent.getUser());
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerLoginNameCheck(PlayerLoginEvent playerLoginEvent) {
    if (this.plugin.getConfigManager().PreventDifferentCapitalizationNames) {
      Player player = playerLoginEvent.getPlayer();
      if (!CMINPCChecker.isNPC(player)) {
        if (!Bukkit.getServer().getOnlineMode()) {
          CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player.getName(), false);
          if (cmiUser != null) {
            if (!cmiUser.getUniqueId().equals(player.getUniqueId())) {
              if (cmiUser.getName(false) != null
                  && !cmiUser.getName(false).equals(player.getName())) {
                playerLoginEvent.disallow(
                    Result.KICK_OTHER,
                    CMILC.info_cantLoginWithDifCap.getLocale(
                        "[oldName]", cmiUser.getName(false), "[currentName]", player.getName()));
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerLogin(PlayerLoginEvent playerLoginEvent) {
    Player player = playerLoginEvent.getPlayer();
    if (!CMINPCChecker.isNPC(player)) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (!cmiUser.getLockedIps().isEmpty()) {
          InetAddress inetAddress = playerLoginEvent.getAddress();
          if (inetAddress != null) {
            String string = inetAddress.getHostAddress();
            if (string != null) {
              if (!cmiUser.getLockedIps().contains(string)) {
                playerLoginEvent.disallow(
                    Result.KICK_OTHER, CMILC.getIM(lockip.class, "CantLogin"));
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerLoginIpRecord(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (!CMINPCChecker.isNPC(player)) {
      if (Config.IPRecord) {
        CMIScheduler.runLaterAsync(
            this.plugin,
            () -> this.recordIP(player),
            Bukkit.getOnlineMode() ? 20L : this.plugin.getConfigManager().IPdelay * 20L);
      }
    }
  }

  private void recordIP(Player player) {
    this.recordIP(player, player.getAddress().getAddress());
  }

  private void recordIP(Player player, InetAddress inetAddress) {
    if (player.isOnline()) {
      String string = inetAddress.getHostAddress();
      if (string != null) {
        CMIUser cmiUser = CMIUser.getUser(player);
        if (cmiUser != null) {
          cmiUser.addIps(string);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
    Player player = playerDeathEvent.getEntity();
    if (!CMINPCChecker.isNPC(player)) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        cmiUser.setDeathLoc(new CMILocation(player.getLocation()));
        if (PermissionsManager.CMIPerm.command_back_ondeath.hasPermission(player)) {
          cmiUser.setLastTeleportLocation(player.getLocation(), false);
          cmiUser.setBungeeBackLocation(null);
        }

        cmiUser.addForDelayedSave();
        if (Version.isCurrentEqualOrLower(Version.v1_15_R1)) {
          CMIScheduler.runTaskLater(this.plugin, cmiUser::respawn, 5L);
        } else {
          CMIScheduler.runTaskLater(this.plugin, cmiUser::respawn, 30L);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void teleportBack(PlayerTeleportEvent playerTeleportEvent) {
    Player player = playerTeleportEvent.getPlayer();
    if (!CMINPCChecker.isNPC(player)) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (playerTeleportEvent.getTo() != null) {
          if (!playerTeleportEvent
                  .getFrom()
                  .getWorld()
                  .equals(playerTeleportEvent.getTo().getWorld())
              || !(playerTeleportEvent.getFrom().distance(playerTeleportEvent.getTo()) < 2.0)) {
            Location location = playerTeleportEvent.getFrom().clone();
            if (cmiUser.isSitting()) {
              location = location.add(0.0, 0.7, 0.0);
            }

            if (!playerTeleportEvent.getCause().name().equalsIgnoreCase("CHORUS_FRUIT")
                && !playerTeleportEvent.getCause().equals(TeleportCause.ENDER_PEARL)
                && !playerTeleportEvent.getCause().equals(TeleportCause.UNKNOWN)
                && (!playerTeleportEvent
                        .getFrom()
                        .getWorld()
                        .equals(playerTeleportEvent.getTo().getWorld())
                    || playerTeleportEvent.getFrom().distance(playerTeleportEvent.getTo())
                        > this.plugin.getTeleportManager().getBackMinDistance())) {
              Long exclusionTime = backExclusionMap.get(player.getUniqueId());
              if (exclusionTime != null && exclusionTime + 100L > System.currentTimeMillis()) {
                return;
              }

              if (!this.plugin
                  .getTeleportManager()
                  .getBackBlackList()
                  .contains(location.getWorld().getName().toLowerCase())) {
                cmiUser.setLastTeleportLocation(location, false);
                cmiUser.setBungeeBackLocation(null);
              }
            }
          }
        }
      }
    }
  }

  public static void addBackLocationExclusion(UUID uuid) {
    backExclusionMap.put(uuid, System.currentTimeMillis());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void teleportBackWECompasss(PlayerInteractEvent playerInteractEvent) {
    if (!this.plugin.getTeleportManager().isBackWithWE()) {
      Player player = playerInteractEvent.getPlayer();
      if (CMIItemStack.getItemInMainHand(player) != null
          && CMIItemStack.getItemInMainHand(player).getType().equals(Material.COMPASS)) {
        if ((!playerInteractEvent.getAction().equals(Action.RIGHT_CLICK_AIR)
                    && !playerInteractEvent.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || player.hasPermission("worldedit.navigation.thru.command"))
            && (!playerInteractEvent.getAction().equals(Action.LEFT_CLICK_AIR)
                    && !playerInteractEvent.getAction().equals(Action.LEFT_CLICK_BLOCK)
                || player.hasPermission("worldedit.navigation.jumpto.command"))) {
          backExclusionMap.put(player.getUniqueId(), System.currentTimeMillis());
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(PlayerMoveEvent playerMoveEvent) {
    if (this.plugin.getConfigManager().isPreventPlayersOnNetherRoof()) {
      if (playerMoveEvent.getTo() != null) {
        if (playerMoveEvent.getTo().getWorld().getEnvironment().equals(Environment.NETHER)) {
          if (playerMoveEvent.getFrom().getBlockX() != playerMoveEvent.getTo().getBlockX()
              || playerMoveEvent.getFrom().getBlockY() != playerMoveEvent.getTo().getBlockY()
              || playerMoveEvent.getFrom().getBlockZ() != playerMoveEvent.getTo().getBlockZ()) {
            Player player = playerMoveEvent.getPlayer();
            Long lastRoofTeleport = this.roofTeleporterDelay.get(player.getUniqueId());
            if (lastRoofTeleport == null
                || lastRoofTeleport + 1000L <= System.currentTimeMillis()) {
              Integer integer =
                  this.plugin.getConfigManager().getNetherRoofHeight() == 0
                      ? CMIWorld.getMaxHeight(playerMoveEvent.getTo().getWorld())
                      : this.plugin.getConfigManager().getNetherRoofHeight();
              if (playerMoveEvent.getTo().getBlockY() >= integer) {
                this.roofTeleporterDelay.put(player.getUniqueId(), System.currentTimeMillis());
                this.plugin
                    .getTeleportHandler()
                    .teleportPlayer(
                        player,
                        player.getLocation().clone().add(0.0, -5.0, 0.0),
                        CMITeleportType.NetherRoof)
                    .thenAccept(
                        cmiTeleportFeedback -> {
                          if (!cmiTeleportFeedback.isSuccess()) {
                            CMILocation cmiLocation = SpawnUtil.getSpawnPoint(player);
                            if (cmiLocation != null) {
                              this.plugin
                                  .getTeleportHandler()
                                  .teleportPlayer(player, cmiLocation, CMITeleportType.NetherRoof);
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

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveBelow(PlayerMoveEvent playerMoveEvent) {
    if (this.plugin.getConfigManager().isPreventPlayersBelowBedrock()) {
      if (playerMoveEvent.getTo() != null) {
        if (playerMoveEvent.getTo().getWorld().getEnvironment().equals(Environment.NETHER)
            || playerMoveEvent.getTo().getWorld().getEnvironment().equals(Environment.NORMAL)) {
          if (playerMoveEvent.getFrom().getBlockX() != playerMoveEvent.getTo().getBlockX()
              || playerMoveEvent.getFrom().getBlockY() != playerMoveEvent.getTo().getBlockY()
              || playerMoveEvent.getFrom().getBlockZ() != playerMoveEvent.getTo().getBlockZ()) {
            if (playerMoveEvent.getTo().getBlockY()
                <= CMIWorld.getMinHeight(playerMoveEvent.getTo().getWorld())) {
              Player player = playerMoveEvent.getPlayer();
              boolean flag = false;
              if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
                try {
                  flag = player.isGliding();
                } catch (Throwable throwable) {
                  throwable.printStackTrace();
                }
              }

              if (player.isFlying() || flag) {
                Long lastRoofTeleport = this.roofTeleporterDelay.get(player.getUniqueId());
                if (lastRoofTeleport == null
                    || lastRoofTeleport + 1000L <= System.currentTimeMillis()) {
                  this.roofTeleporterDelay.put(player.getUniqueId(), System.currentTimeMillis());
                  Location location = player.getLocation().clone();
                  location.setY(64.0);
                  this.plugin
                      .getTeleportHandler()
                      .teleportPlayer(player, location, CMITeleportType.BelowBedrock)
                      .thenAccept(
                          cmiTeleportFeedback -> {
                            if (!cmiTeleportFeedback.isSuccess()) {
                              CMILocation cmiLocation = SpawnUtil.getSpawnPoint(player);
                              if (cmiLocation != null) {
                                this.plugin
                                    .getTeleportHandler()
                                    .teleportPlayer(
                                        player, cmiLocation, CMITeleportType.BelowBedrock);
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

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerFlyToHigh(PlayerMoveEvent playerMoveEvent) {
    if (!playerMoveEvent.isCancelled()) {
      if (!CMIWorldLimits.isFlyAboveRoof()) {
        if (playerMoveEvent.getTo() != null) {
          if (playerMoveEvent.getTo().getBlockY()
              > this.plugin.getUtilManager().getMaxWorldHeight(playerMoveEvent.getTo().getWorld())
                  - 1) {
            if (playerMoveEvent.getFrom().getBlockX() != playerMoveEvent.getTo().getBlockX()
                || playerMoveEvent.getFrom().getBlockY() != playerMoveEvent.getTo().getBlockY()
                || playerMoveEvent.getFrom().getBlockZ() != playerMoveEvent.getTo().getBlockZ()) {
              if (playerMoveEvent.getFrom().getBlockY() <= playerMoveEvent.getTo().getBlockY()) {
                Player player = playerMoveEvent.getPlayer();
                boolean flag = false;
                if (player.isFlying()) {
                  flag = true;
                }

                if (!flag && Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
                  try {
                    if (player.isGliding()) {
                      flag = true;
                    }
                  } catch (Throwable throwable) {
                  }
                }

                if (flag) {
                  if (PermissionsManager.CMIPerm.worldlimit_fly_aboveroof.hasPermission(
                      player, false, 10000L)) {
                    return;
                  }

                  if (this.informer.get(player.getUniqueId()) == null
                      || this.informer.get(player.getUniqueId()) < System.currentTimeMillis()) {
                    CMILC.info_flyingToHigh.sendMessage(
                        player,
                        "[max]",
                        this.plugin
                            .getUtilManager()
                            .getMaxWorldHeight(playerMoveEvent.getTo().getWorld()));
                    this.informer.put(player.getUniqueId(), System.currentTimeMillis() + 5000L);
                  }

                  playerMoveEvent.setCancelled(true);
                }
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    if (!CMINPCChecker.isNPC(player)) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        backExclusionMap.remove(player.getUniqueId());
        this.roofTeleporterDelay.remove(player.getUniqueId());
        this.informer.remove(player.getUniqueId());
        this.plugin.getPlayerManager().removeCommandSpy(cmiUser.getUniqueId());
        this.plugin.getPlayerManager().removeSignSpy(cmiUser.getUniqueId());
        this.plugin.getPlayerManager().removeCommandSpyDelayed(cmiUser.getUniqueId());
        this.plugin.getPlayerManager().removeSocialSpy(cmiUser.getUniqueId());
        this.plugin.getPlayerManager().removeSocialSpyDelayed(cmiUser.getUniqueId());
        lastonline.addLogOff(cmiUser);
        cmiUser.setLogOutLocation(player.getLocation());
        cmiUser.setLastLogoff(System.currentTimeMillis());
        cmiUser.getTotalPlayTime();
        cmiUser.addForDelayedSave();
        Team team = this.plugin.getSB().getTeam("CMINP" + cmiUser.getId());
        if (team != null) {
          try {
            team.unregister();
          } catch (Exception exception) {
          }
        }

        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (!cmiUser.isOnline()) {
                cmiUser.setPlayer(null);
              }
            },
            20L);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void removeFromRankCheck(PlayerQuitEvent playerQuitEvent) {
    this.plugin.getRankManager().removeFromCheck(playerQuitEvent.getPlayer().getUniqueId());
    this.plugin.getPlayerManager().removeSocialSpy(playerQuitEvent.getPlayer().getUniqueId());
    if (this.plugin.getDbManager().isForceSaveOnLogOut()) {
      try {
        CMIUser.getUser(playerQuitEvent.getPlayer()).addForDelayedSave();
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }

      this.plugin.getDbManager().saveBatchAsync(true);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerLogin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (!CMINPCChecker.isNPC(player)) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (this.plugin.getDbManager().isForceLoadOnLogIn()) {
          CMIScheduler.runTaskLater(
              this.plugin,
              () ->
                  this.plugin
                      .getDbManager()
                      .loadUserAsync(player.getUniqueId())
                      .thenRun(
                          () ->
                              CMIScheduler.runTask(
                                  this.plugin, () -> this.processPlayerLogin(cmiUser))),
              5L);
        } else {
          this.processPlayerLogin(cmiUser);
        }
      }
    }
  }

  private void processPlayerLogin(CMIUser cmiUser) {
    cmiUser.setLastLogin(System.currentTimeMillis());
    if (cmiUser.getPlayerTime() > 0L && cmiUser.isOnline()) {
      cmiUser.getPlayer().setPlayerTime(cmiUser.getPlayerTime(), false);
    }

    CMIScheduler.runTaskLater(this.plugin, cmiUser::recalculateRank, 20L);
    lastonline.addLogOff(cmiUser);
    if (Version.isCurrentHigher(Version.v1_8_R3)) {
      cmiUser.reapplyNamePlate();
    }

    if (cmiUser.getOptionState(PlayerOption.chatSpy)) {
      this.plugin.getPlayerManager().addSocialSpyDelayed(cmiUser.getUniqueId());
    }

    if (cmiUser.getOptionState(PlayerOption.cmdSpy)) {
      this.plugin.getPlayerManager().addCommandSpyDelayed(cmiUser.getUniqueId());
    }

    if (cmiUser.getOptionState(PlayerOption.signSpy)) {
      this.plugin.getPlayerManager().addSignSpyDelayed(cmiUser.getUniqueId());
    }

    cmiUser.updateDisplayName();
    CMIScheduler.runTaskLater(
        this.plugin,
        () -> {
          if (!cmiUser.getMails().isEmpty() && cmiUser.isOnline()) {
            RawMessage rawMessage = new RawMessage();
            rawMessage
                .addText(
                    CMILC.getIM(mail.class, "waitingMail", "[amount]", cmiUser.getMails().size()))
                .addHover(LC.info_Click.getLocale(new Object[0]))
                .addCommand("cmi mail");
            rawMessage.show(cmiUser.getPlayer(false));
          }
        },
        40L);
    if (cmiUser.isOnline()) {
      if (cmiUser.getAlertUntil() > 0L) {
        var list = CMILC.getIML(alert.class, "inform");
        String string = null;
        Snd snd = new Snd();
        snd.setTarget(cmiUser);
        list = this.plugin.getLM().updateSnd(snd, list);
        list = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(cmiUser.getPlayer(), list);
        if (cmiUser.getAlertReason() != null) {
          string =
              CMILC.getIM(
                  alert.class,
                  "reason",
                  "[reason]",
                  CMIChatColor.translate(cmiUser.getAlertReason()),
                  "[reasson]",
                  CMIChatColor.translate(cmiUser.getAlertReason()));
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
          if (PermissionsManager.CMIPerm.command_alert_inform.hasPermission(player)) {
            for (String line : list) {
              player.sendMessage(line.replace("[name]", cmiUser.getName()));
            }

            if (string != null) {
              player.sendMessage(string);
            }

            if (this.plugin.getConfigManager().isNotesShowOnAlertEvent()
                && !cmiUser.getNotes().isEmpty()) {
              snd = new Snd().setSender(player).setTarget(cmiUser);
              RawMessage messageBuilder = new RawMessage();
              messageBuilder
                  .addText(
                      CMILC.getIM(
                          patrol.class, "notes", snd, "[amount]", cmiUser.getNotes().size()))
                  .addHover(LC.info_Click.getLocale(new Object[0]))
                  .addCommand("cmi note " + cmiUser.getName() + " list");
              messageBuilder.show(player);
            }
          }
        }

        if (System.currentTimeMillis() > cmiUser.getAlertUntil()) {
          cmiUser.setAlertUntil(0L);
        }
      }

      if (this.plugin.getConfigManager().getMotd() != null) {
        this.plugin
            .getCTextManager()
            .showCText(cmiUser.getPlayer(false), this.plugin.getConfigManager().getMotd(), 1);
      }

      try {
        if (this.plugin.getConfigManager().isPotionEffectsDeductWhileOffline()
            && cmiUser.getLastLogoff() > 0L) {
          long longValue = System.currentTimeMillis() - cmiUser.getLastLogoff();
          longValue /= 50L;

          for (PotionEffect potionEffect : cmiUser.getPlayer().getActivePotionEffects()) {
            cmiUser.getPlayer().removePotionEffect(potionEffect.getType());
            if (potionEffect.getDuration() > longValue) {
              PotionEffect newEffect =
                  new PotionEffect(
                      potionEffect.getType(),
                      (int) (potionEffect.getDuration() - longValue),
                      potionEffect.getAmplifier(),
                      potionEffect.isAmbient(),
                      potionEffect.hasParticles());
              cmiUser.getPlayer().addPotionEffect(newEffect);
            }
          }
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }
}
