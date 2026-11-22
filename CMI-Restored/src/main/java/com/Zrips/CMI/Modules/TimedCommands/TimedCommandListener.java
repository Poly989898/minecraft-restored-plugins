package com.Zrips.CMI.Modules.TimedCommands;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIGod;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIWorldLimits;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class TimedCommandListener implements Listener {
  private CMI plugin;

  public TimedCommandListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerLogOff(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      this.plugin.getTimedCommandManager().checkTFly(cmiUser, true);
      this.plugin.getTimedCommandManager().checkTGod(cmiUser, true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void EntityDamageEvent(EntityDamageEvent entityDamageEvent) {
    if (entityDamageEvent.getEntity() instanceof Player) {
      if (!entityDamageEvent.isCancelled()) {
        Player player = (Player) entityDamageEvent.getEntity();
        CMIUser cmiUser = CMIUser.getUser(player);
        if (cmiUser != null) {
          if (cmiUser.getTgod() != 0L) {
            entityDamageEvent.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void PlayerLoginEvent(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      if (cmiUser.getTgod() > System.currentTimeMillis()) {
        int value = (int) ((cmiUser.getTgod() - System.currentTimeMillis()) / 1000L * 20L);
        if (value >= 0) {
          CMIScheduler.runAtEntity(
              this.plugin, player, () -> CMIGod.applyNoDamageTick(player, value));
        } else {
          CMIScheduler.runAtEntity(
              this.plugin, player, () -> CMIGod.forceApplyNoDamageTick(player, 1));
        }

        this.plugin.getTimedCommandManager().addTimed(cmiUser);
      }

      if (cmiUser.getTfly() != 0L) {
        if (cmiUser.getTfly() > System.currentTimeMillis()) {
          this.plugin.getTimedCommandManager().addTimed(cmiUser);
          player.setAllowFlight(true);
        } else {
          cmiUser.setAllowFlight(false);
          cmiUser.setFlying(false);
          cmiUser.setWasFlying(false);
          cmiUser.setHadAllowFlight(false);
          if (this.plugin.getTeleportHandler().needToFly(player)) {
            this.plugin
                .getTeleportHandler()
                .teleportPlayer(player, player.getLocation(), TeleportCause.PLUGIN, true);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void EntityTargetLivingEntityEvent(
      EntityTargetLivingEntityEvent entityTargetLivingEntityEvent) {
    if (!entityTargetLivingEntityEvent.isCancelled()) {
      try {
        if (!entityTargetLivingEntityEvent.getReason().equals(TargetReason.CLOSEST_PLAYER)
                && !entityTargetLivingEntityEvent
                    .getReason()
                    .equals(TargetReason.OWNER_ATTACKED_TARGET)
                && !entityTargetLivingEntityEvent
                    .getReason()
                    .equals(TargetReason.TARGET_ATTACKED_ENTITY)
            || !entityTargetLivingEntityEvent.getEntity().getType().isAlive()) {
          return;
        }

        if (entityTargetLivingEntityEvent.getTarget() == null) {
          return;
        }

        if (entityTargetLivingEntityEvent.getTarget().getType() != EntityType.PLAYER) {
          return;
        }

        CMIUser cmiUser = CMIUser.getUser((Player) entityTargetLivingEntityEvent.getTarget());
        if (cmiUser == null) {
          return;
        }

        if (cmiUser.isTgod()) {
          entityTargetLivingEntityEvent.setCancelled(true);
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void PlayerLoginEvent(PlayerChangedWorldEvent playerChangedWorldEvent) {
    Player player = playerChangedWorldEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      if (cmiUser.getTfly() > System.currentTimeMillis()) {
        player.setAllowFlight(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onRespawn(PlayerRespawnEvent playerRespawnEvent) {
    Player player = playerRespawnEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      if (CMIWorldLimits.isWorldLimits()) {
        World world = playerRespawnEvent.getRespawnLocation().getWorld();
        if (Version.isCurrentEqualOrHigher(Version.v1_16_R3)
            && !PermissionsManager.CMIPerm.worldlimit_fly_bypass.hasPermission(player)
            && CMIWorldLimits.isFlyModeLimited(world.getName())
            && !CMIWorldLimits.getWorldFlyMode(world.getName())) {
          return;
        }
      }

      if (cmiUser.getTfly() != 0L) {
        if (cmiUser.getTfly() > System.currentTimeMillis()) {
          this.plugin.getTimedCommandManager().addTimed(cmiUser);
          player.setAllowFlight(true);
        } else {
          player.setAllowFlight(false);
        }
      }
    }
  }
}
