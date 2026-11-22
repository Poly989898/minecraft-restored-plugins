package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIWorldLimits;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class WorldLimitsElytra implements Listener {
  private CMI plugin;

  public WorldLimitsElytra(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler
  public void onWorldChangeElytra(PlayerChangedWorldEvent playerChangedWorldEvent) {}

  @EventHandler
  public void onLoginElytra(PlayerLoginEvent playerLoginEvent) {}

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerMoveEvent(PlayerMoveEvent playerMoveEvent) {
    if (CMIWorldLimits.isWorldLimits()
        && playerMoveEvent.getTo() != null
        && !playerMoveEvent.getFrom().getChunk().equals(playerMoveEvent.getTo().getChunk())) {
      Player player = playerMoveEvent.getPlayer();
      if (player.isGliding()) {
        World world = player.getWorld();
        if (CMIWorldLimits.isElytraModeLimited(world.getName())) {
          if (!CMIWorldLimits.getWorldElytraMode(world.getName())) {
            if (!PermissionsManager.CMIPerm.worldlimit_elytra_bypass.hasPermission(player)) {
              CMILC.Elytra_CantGlide.sendMessage(player);
              player.setGliding(false);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void EntityToggleGlideEvent(EntityToggleGlideEvent entityToggleGlideEvent) {
    if (CMIWorldLimits.isWorldLimits() && entityToggleGlideEvent.getEntity() instanceof Player) {
      Player player = (Player) entityToggleGlideEvent.getEntity();
      World world = player.getWorld();
      if (CMIWorldLimits.isElytraModeLimited(world.getName())) {
        if (!CMIWorldLimits.getWorldElytraMode(world.getName())) {
          if (!PermissionsManager.CMIPerm.worldlimit_elytra_bypass.hasPermission(player)) {
            CMILC.Elytra_CantGlide.sendMessage(player);
            entityToggleGlideEvent.setCancelled(true);
            player.setGliding(false);
            CMIScheduler.runTaskLater(this.plugin, () -> player.setGliding(false), 1L);
            CMIScheduler.runTaskLater(
                this.plugin,
                () -> {
                  if (player.isOnline() && world.equals(player.getWorld())) {
                    player.setGliding(false);
                  }
                },
                10L);
          }
        }
      }
    }
  }
}
