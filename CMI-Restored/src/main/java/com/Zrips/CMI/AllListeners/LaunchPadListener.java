package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.list.launch;
import com.Zrips.CMI.commands.list.util;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerTeleportEvent;

public class LaunchPadListener implements Listener {
  private CMI plugin;

  public LaunchPadListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onTeleport(PlayerTeleportEvent playerTeleportEvent) {
    if (!launch.fall.isEmpty()) {
      launch.fall.remove(playerTeleportEvent.getPlayer().getUniqueId());
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void EntityDamageEvent(EntityDamageEvent entityDamageEvent) {
    if (!entityDamageEvent.isCancelled()) {
      if (DamageCause.FALL.equals(entityDamageEvent.getCause())) {
        if (!launch.fall.isEmpty()) {
          Long launchTime = launch.fall.remove(entityDamageEvent.getEntity().getUniqueId());
          if (launchTime != null) {
            if (System.currentTimeMillis() - launchTime <= 15000L) {
              if (util.imortaldebug) {
                CMIMessages.consoleMessage(
                    "LaunchPad EntityDamageEvent->EntityDamageByEntityEvent");
              }

              entityDamageEvent.setDamage(0.0);
              entityDamageEvent.setCancelled(true);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onTeleportLaunch(PlayerTeleportEvent playerTeleportEvent) {
    launch.map.remove(playerTeleportEvent.getPlayer().getUniqueId());
  }
}
