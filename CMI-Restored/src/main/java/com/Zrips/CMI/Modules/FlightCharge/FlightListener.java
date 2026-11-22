package com.Zrips.CMI.Modules.FlightCharge;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIWorldLimits;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.flyc;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.BossBar.CMIBossBarHideEvent;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class FlightListener implements Listener {
  private CMI plugin;
  static HashMap<UUID, traveledDistance> distanceMap = new HashMap<>();
  public static HashMap<UUID, freeFall> fallDistanceMap = new HashMap<>();
  HashMap<UUID, Long> informed = new HashMap<>();

  public FlightListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerToggleFlightEvent(PlayerToggleFlightEvent playerToggleFlightEvent) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerToggleFlightEvent.getPlayer());
    if (cmiUser != null && cmiUser.getFlightCharge().isEnabled()) {
      if (playerToggleFlightEvent.isFlying()) {
        traveledDistance traveledDistance =
            new traveledDistance(
                this.plugin.getPlayerManager().getUser(playerToggleFlightEvent.getPlayer()));
        distanceMap.put(playerToggleFlightEvent.getPlayer().getUniqueId(), traveledDistance);
        this.plugin.getFlightChargeManager().process(traveledDistance, null);
      } else {
        traveledDistance finishedDistance =
            distanceMap.remove(playerToggleFlightEvent.getPlayer().getUniqueId());
        if (finishedDistance != null) {
          this.plugin.getFlightChargeManager().process(finishedDistance, null);
        }

        BossBarInfo bossBarInfo = cmiUser.getBossBar("CMIFlightChargeBossBar");
        if (bossBarInfo != null) {
          bossBarInfo.setKeepForTicks(20);
          cmiUser.addBossBar(bossBarInfo);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerMoveEvent2(PlayerMoveEvent playerMoveEvent) {
    if (this.plugin.getFlightChargeManager().isDamageOnFall()) {
      Player player = playerMoveEvent.getPlayer();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerMoveEvent.getPlayer());
      if (cmiUser != null && cmiUser.getFlightCharge().isEnabled() && cmiUser.getTfly() <= 0L) {
        if (player.isFlying()) {
          freeFall freeFall = fallDistanceMap.get(player.getUniqueId());
          if (freeFall == null) {
            fallDistanceMap.put(player.getUniqueId(), new freeFall().setJump(false));
          } else {
            freeFall.setJump(false);
          }
        } else if (player.getAllowFlight()) {
          if (!(cmiUser.getFlightCharge().getCharge() <= 0.0)) {
            freeFall freeFallState = fallDistanceMap.get(player.getUniqueId());
            if (freeFallState == null) {
              fallDistanceMap.put(
                  player.getUniqueId(), new freeFall().setFallDistance(player.getFallDistance()));
            } else if (freeFallState.getFallDistance() > 3.0F
                && player.isOnGround()
                && (player.getGameMode().equals(GameMode.SURVIVAL)
                    || player.getGameMode().equals(GameMode.ADVENTURE))
                && CMIMaterial.get(player.getLocation().clone().add(0.0, -1.0, 0.0).getBlock())
                    != CMIMaterial.SLIME_BLOCK
                && !CMIMaterial.isBed(player.getLocation().getBlock().getType())
                && !CMIMaterial.isBed(
                    player.getLocation().clone().add(0.0, -1.0, 0.0).getBlock().getType())
                && player.getNoDamageTicks() <= 0) {
              int value = (int) (freeFallState.getFallDistance() - 3.0F);
              int fallDistanceInt = value;
              if (this.plugin.getFlightChargeManager().isDamageOnFall()) {
                double number = player.getHealth();
                if (!this.plugin.getFlightChargeManager().isKillOnFall() && value > number - 1.0) {
                  value = (int) (number - 1.0);
                }

                float floatValue = player.getFallDistance();
                player.setFallDistance(freeFallState.getFallDistance());
                EntityDamageEvent entityDamageEvent =
                    new EntityDamageEvent(player, DamageCause.FALL, value);
                Bukkit.getServer().getPluginManager().callEvent(entityDamageEvent);
                player.setFallDistance(floatValue);
                if (entityDamageEvent.isCancelled()) {
                  return;
                }

                value = (int) entityDamageEvent.getFinalDamage();
                freeFallState.setFallDistance(0.0F);
                if (freeFallState.getJump()
                    || this.plugin.getFlightChargeManager().isDamageOnToggle()) {
                  player.damage(value);
                }
              }

              distanceMap.remove(playerMoveEvent.getPlayer().getUniqueId());
              this.plugin
                  .getFlightChargeManager()
                  .process(
                      player,
                      fallDistanceInt
                          * this.plugin.getFlightChargeManager().getDeductOnFallMulti());
            } else {
              if (player.isOnGround()) {
                freeFallState.setJump(true);
              }

              freeFallState.setFallDistance(player.getFallDistance());
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerTeleportEvent(PlayerTeleportEvent playerTeleportEvent) {
    distanceMap.remove(playerTeleportEvent.getPlayer().getUniqueId());
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerMoveEvent(PlayerMoveEvent playerMoveEvent) {
    Player player = playerMoveEvent.getPlayer();
    if (player.isFlying()) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerMoveEvent.getPlayer());
      if (cmiUser != null && cmiUser.getFlightCharge().isEnabled() && cmiUser.getTfly() <= 0L) {
        traveledDistance traveledDistance = distanceMap.get(player.getUniqueId());
        if (traveledDistance == null) {
          distanceMap.put(playerMoveEvent.getPlayer().getUniqueId(), new traveledDistance(cmiUser));
        } else if (!traveledDistance.toSoonToCheck()) {
          this.plugin.getFlightChargeManager().process(traveledDistance, null);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    distanceMap.remove(playerQuitEvent.getPlayer().getUniqueId());
    fallDistanceMap.remove(playerQuitEvent.getPlayer().getUniqueId());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void PlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      if (cmiUser.getFlightCharge().getCharge() > 0.0 && cmiUser.getFlightCharge().isEnabled()) {
        if (this.plugin.getFlightChargeManager().isFlightChargeEnabledByDefault()) {
          player.setAllowFlight(true);
        } else {
          CMIScheduler.runTask(
              this.plugin,
              () -> {
                if (player.isOnline()
                    && cmiUser.getFlightCharge().isEnabled()
                    && PermissionsManager.CMIPerm.command_fly_safelogin.hasPermission(player)) {
                  player.setAllowFlight(true);
                }
              });
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerJoinEvent(PlayerChangedWorldEvent playerChangedWorldEvent) {
    Player player = playerChangedWorldEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      if (cmiUser.getFlightCharge().getCharge() > 0.0
          && cmiUser.getFlightCharge().isEnabled()
          && this.plugin.getFlightChargeManager().isFlightChargeEnabledByDefault()) {
        player.setAllowFlight(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void PlayerGameModeChangeEvent(PlayerGameModeChangeEvent playerGameModeChangeEvent) {
    Player player = playerGameModeChangeEvent.getPlayer();
    if (this.plugin.getFlightChargeManager().isFlightChargeAutoSwitch()) {
      CMIUser cmiUser = CMIUser.getUser(player);
      if (cmiUser != null) {
        Snd snd = new Snd().setSender(player).setTarget(player);
        if (playerGameModeChangeEvent.getNewGameMode().name().equalsIgnoreCase("Creative")
            || playerGameModeChangeEvent.getNewGameMode().name().equalsIgnoreCase("Spectator")) {
          cmiUser.getFlightCharge().setEnabled(false);
          if (!this.informed.containsKey(player.getUniqueId())
              || this.informed.get(player.getUniqueId()) + 1000L < System.currentTimeMillis()) {
            CMILC.info(flyc.class, player, "targetFeedbackSelf", snd, "[boolean]", false);
            this.informed.put(player.getUniqueId(), System.currentTimeMillis());
          }
        } else if ((playerGameModeChangeEvent.getNewGameMode().name().equalsIgnoreCase("Survival")
                || playerGameModeChangeEvent.getNewGameMode().name().equalsIgnoreCase("Adventure"))
            && cmiUser.getFlightCharge().getCharge() > 0.0) {
          cmiUser.getFlightCharge().setEnabled(true);
          if (!player.isOnGround()) {
            CMIScheduler.runTaskLater(
                this.plugin,
                () -> {
                  cmiUser.setAllowFlight(true);
                  if (player != null) {
                    player.setFlying(true);
                  }
                },
                1L);
          }

          if (!this.informed.containsKey(player.getUniqueId())
              || this.informed.get(player.getUniqueId()) + 1000L < System.currentTimeMillis()) {
            CMILC.info(flyc.class, player, "targetFeedbackSelf", snd, "[boolean]", true);
            this.informed.put(player.getUniqueId(), System.currentTimeMillis());
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void CMIBossBarHideEvent(CMIBossBarHideEvent cmiBossBarHideEvent) {
    if (cmiBossBarHideEvent.getBossBar() != null
        && cmiBossBarHideEvent.getBossBar().getNameOfBar().equals("CMIFlightChargeBossBar")
        && cmiBossBarHideEvent.getBossBar().getPlayer() != null
        && cmiBossBarHideEvent.getBossBar().getPlayer().isOnline()) {
      Player player = cmiBossBarHideEvent.getBossBar().getPlayer();
      if (player != null) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        if (cmiUser != null && cmiUser.isOnline()) {
          boolean flag =
              player.isFlying() && this.plugin.getFlightChargeManager().getDeductOnIdling() != 0.0;
          if (player.getVehicle() == null && cmiUser.getFlightCharge().getCharge() > 0.0 && flag) {
            cmiBossBarHideEvent.setCancelled(true);
          }

          this.plugin.getFlightChargeManager().process(player, flag ? null : -1);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onRespawn(PlayerRespawnEvent playerRespawnEvent) {
    Player player = playerRespawnEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      CMIScheduler.runAtEntity(
          this.plugin,
          player,
          () -> {
            if (CMIWorldLimits.isWorldLimits()) {
              World world = playerRespawnEvent.getRespawnLocation().getWorld();
              if (Version.isCurrentEqualOrHigher(Version.v1_16_R3)
                  && !PermissionsManager.CMIPerm.worldlimit_fly_bypass.hasPermission(player)
                  && CMIWorldLimits.isFlyModeLimited(world.getName())
                  && !CMIWorldLimits.getWorldFlyMode(world.getName())) {
                return;
              }
            }

            if (cmiUser.getFlightCharge().isEnabled()) {
              if (player.isOnline()
                  && cmiUser.getFlightCharge().getCharge() > 0.0
                  && cmiUser.getFlightCharge().isEnabled()) {
                player.setAllowFlight(true);
              }
            }
          });
    }
  }
}
