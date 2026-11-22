package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIWorldLimits;
import com.Zrips.CMI.Containers.worldFlyState;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WorldLimits implements Listener {
  private CMI plugin;

  public WorldLimits(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler
  public void onWorldChangeGameMode(PlayerChangedWorldEvent playerChangedWorldEvent) {
    this.processGameMode(playerChangedWorldEvent.getPlayer());
  }

  @EventHandler
  public void onLoginGameMode(PlayerLoginEvent playerLoginEvent) {
    this.processGameMode(playerLoginEvent.getPlayer());
  }

  private void processGameMode(Player player) {
    if (CMIWorldLimits.isWorldLimits()) {
      World world = player.getWorld();
      if (CMIWorldLimits.isGameModeLimited(world.getName())
          && !PermissionsManager.CMIPerm.worldlimit_gamemode_bypass.hasPermission(player)) {
        player.setGameMode(CMIWorldLimits.getWorldGameMode(world.getName()));
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onWorldChangeFly(PlayerChangedWorldEvent playerChangedWorldEvent) {
    this.processFly(
        playerChangedWorldEvent.getPlayer(), playerChangedWorldEvent.getFrom().getName());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onWorldChangeFlyCached(PlayerChangedWorldEvent playerChangedWorldEvent) {
    this.processCachedFly(playerChangedWorldEvent.getPlayer(), playerChangedWorldEvent.getFrom());
  }

  private void processCachedFly(Player player, World world) {
    World playerWorld = player.getWorld();
    boolean flag = player.getAllowFlight();
    if (!CMIWorldLimits.isWorldLimits()
        || !CMIWorldLimits.isFlyModeLimited(playerWorld.getName())) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        worldFlyState worldFlyState = cmiUser.removeFlyCachedMode(playerWorld.getName());
        if (worldFlyState != null) {
          if (worldFlyState.isFlyEnabled()
              && worldFlyState.getGameMode().equals(player.getGameMode())) {
            cmiUser.setAllowFlight(true);
            if (!player.isOnGround() && player.isOnline()) {
              player.setFallDistance(0.0F);
              player.setFlying(true);
            }
          }
        } else {
          worldFlyState otherFlyState = cmiUser.removeFlyCachedMode(world.getName());
          if (flag
              || otherFlyState != null
                  && player.getGameMode().equals(otherFlyState.getGameMode())
                  && otherFlyState.isFlyEnabled()) {
            cmiUser.setAllowFlight(true);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void PlayerDeathEventHunger(PlayerDeathEvent playerDeathEvent) {
    if (CMIWorldLimits.isReenabledFlyAfterDeath()) {
      CMIUser cmiUser = CMIUser.getUser(playerDeathEvent.getEntity());
      if (playerDeathEvent.getEntity().getAllowFlight()) {
        cmiUser.addFlyToCache(
            playerDeathEvent.getEntity().getWorld().getName(),
            playerDeathEvent.getEntity().getAllowFlight(),
            true);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onRespawnSetLocation(PlayerRespawnEvent playerRespawnEvent) {
    if (CMIWorldLimits.isReenabledFlyAfterDeath()) {
      World world = playerRespawnEvent.getPlayer().getWorld();
      CMIScheduler.runAtLocation(
          this.plugin,
          playerRespawnEvent.getRespawnLocation(),
          () -> this.processCachedFly(playerRespawnEvent.getPlayer(), world));
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onTeleport(PlayerTeleportEvent playerTeleportEvent) {
    if (playerTeleportEvent.getTo() != null
        && !playerTeleportEvent
            .getFrom()
            .getWorld()
            .equals(playerTeleportEvent.getTo().getWorld())) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerTeleportEvent.getPlayer());
      if (playerTeleportEvent.getFrom().getWorld() != null
          && playerTeleportEvent.getPlayer().getAllowFlight()) {
        cmiUser.addFlyToCache(
            playerTeleportEvent.getFrom().getWorld().getName(),
            playerTeleportEvent.getPlayer().getAllowFlight(),
            true);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLoginFly(PlayerLoginEvent playerLoginEvent) {
    CMIScheduler.runTaskLater(
        this.plugin,
        () -> {
          if (playerLoginEvent.getPlayer().isOnline()) {
            this.processFly(
                playerLoginEvent.getPlayer(), playerLoginEvent.getPlayer().getWorld().getName());
            if (playerLoginEvent.getPlayer().getAllowFlight()) {
              CMIUser cmiUser = CMIUser.getUser(playerLoginEvent.getPlayer());
              if (cmiUser != null) {
                cmiUser.addFlyToCache(
                    playerLoginEvent.getPlayer().getWorld().getName(),
                    playerLoginEvent.getPlayer().getAllowFlight(),
                    false);
              }
            }
          }
        },
        10L);
  }

  private void processFly(Player player, String string) {
    if (CMIWorldLimits.isWorldLimits()) {
      World world = player.getWorld();
      if (CMIWorldLimits.isFlyModeLimited(world.getName())
          && !PermissionsManager.CMIPerm.worldlimit_fly_bypass.hasPermission(player)) {
        if (CMIWorldLimits.getWorldFlyMode(world.getName())) {
          player.setAllowFlight(true);
          player.setFlying(true);
        } else if (!player.getGameMode().toString().equalsIgnoreCase("SPECTATOR")) {
          player.setFlying(false);
          player.setAllowFlight(false);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLoginGod(PlayerLoginEvent playerLoginEvent) {
    this.processGod(playerLoginEvent.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onWorldChangeGod(PlayerChangedWorldEvent playerChangedWorldEvent) {
    this.processGod(playerChangedWorldEvent.getPlayer());
  }

  private void processGod(Player player) {
    if (CMIWorldLimits.isWorldLimits()) {
      World world = player.getWorld();
      if (CMIWorldLimits.isGodModeLimited(world.getName())
          && !PermissionsManager.CMIPerm.worldlimit_god_bypass.hasPermission(player)) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        if (cmiUser != null) {
          CMIScheduler.runTaskLater(
              this.plugin,
              () -> {
                if (cmiUser.isOnline()) {
                  boolean flag = CMIWorldLimits.getWorldGodMode(world.getName());
                  this.plugin.getNMS().changeGodMode(cmiUser.getPlayer(), flag);
                }
              },
              10L);
        }
      }
    }
  }
}
