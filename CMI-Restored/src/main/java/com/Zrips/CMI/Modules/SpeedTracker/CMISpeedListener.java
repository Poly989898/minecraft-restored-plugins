package com.Zrips.CMI.Modules.SpeedTracker;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class CMISpeedListener implements Listener {
  private CMI plugin;

  public CMISpeedListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    CMISpeedTracker.unregister(playerQuitEvent.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onVehicleEnterEvent(VehicleEnterEvent vehicleEnterEvent) {
    if (vehicleEnterEvent.getEntered() instanceof Player) {
      CMIUser cmiUser = CMIUser.getUser((Player) vehicleEnterEvent.getEntered());
      if (cmiUser != null) {
        if (cmiUser.getOptionState(PlayerOption.showVehicleSpeed)) {
          CMISpeedTracker.register((Player) vehicleEnterEvent.getEntered());
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onVehicleExitEvent(VehicleExitEvent vehicleExitEvent) {
    if (vehicleExitEvent.getExited() instanceof Player) {
      CMISpeedTracker.unregister((Player) vehicleExitEvent.getExited());
    }
  }
}
