package com.Zrips.CMI.Modules.SpeedTracker;

import com.Zrips.CMI.CMI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class CMISpeedListener1_9 implements Listener {
  private CMI plugin;

  public CMISpeedListener1_9(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void EntityToggleGlideEvent(EntityToggleGlideEvent entityToggleGlideEvent) {
    if (entityToggleGlideEvent.getEntity() instanceof Player) {
      Player player = (Player) entityToggleGlideEvent.getEntity();
    }
  }
}
