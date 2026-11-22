package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.list.launch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class LaunchPadListener1_9 implements Listener {
  private CMI plugin;

  public LaunchPadListener1_9(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void EntityToggleGlideEvent(EntityToggleGlideEvent entityToggleGlideEvent) {
    if (entityToggleGlideEvent.getEntity() instanceof Player) {
      Player player = (Player) entityToggleGlideEvent.getEntity();
      launch.map.remove(player.getUniqueId());
    }
  }
}
