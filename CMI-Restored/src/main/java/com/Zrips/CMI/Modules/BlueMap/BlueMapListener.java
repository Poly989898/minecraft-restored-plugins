package com.Zrips.CMI.Modules.BlueMap;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.events.CMIWarpCreateEvent;
import com.Zrips.CMI.events.CMIWarpRemoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BlueMapListener implements Listener {
  private CMI plugin;

  public BlueMapListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCMIWarpCreateEvent(CMIWarpCreateEvent cmiWarpCreateEvent) {
    this.plugin.getBlueMapManager().addWarp(cmiWarpCreateEvent.getWarp());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCMIWarpRemoveEvent(CMIWarpRemoveEvent cmiWarpRemoveEvent) {
    this.plugin.getBlueMapManager().removeWarp(cmiWarpRemoveEvent.getWarp());
  }
}
