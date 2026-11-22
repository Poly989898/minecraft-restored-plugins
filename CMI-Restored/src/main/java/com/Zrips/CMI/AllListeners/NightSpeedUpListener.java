package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.SleepStats;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

public class NightSpeedUpListener implements Listener {
  private CMI plugin;

  public NightSpeedUpListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerBedEnterEvent(PlayerBedEnterEvent playerBedEnterEvent) {
    if (!playerBedEnterEvent.isCancelled()) {
      Player player = playerBedEnterEvent.getPlayer();
      if (SleepStats.SleepingSpeedup
          && player.getWorld().getEnvironment().equals(Environment.NORMAL)
          && (SleepStats.timeSpeedUpWorlds.isEmpty()
              || SleepStats.timeSpeedUpWorlds.contains(
                  player.getWorld().getName().toLowerCase()))) {
        new SleepStats(player.getWorld()).onBedEnter(player);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerBedLeaveEvent(PlayerBedLeaveEvent playerBedLeaveEvent) {
    Player player = playerBedLeaveEvent.getPlayer();
    if (SleepStats.SleepingSpeedup
        && player.getWorld().getEnvironment().equals(Environment.NORMAL)
        && (SleepStats.timeSpeedUpWorlds.isEmpty()
            || SleepStats.timeSpeedUpWorlds.contains(player.getWorld().getName().toLowerCase()))) {
      new SleepStats(player.getWorld()).onBedLeave(player);
    }
  }
}
