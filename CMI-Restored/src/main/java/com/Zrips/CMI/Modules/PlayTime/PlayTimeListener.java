package com.Zrips.CMI.Modules.PlayTime;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayTimeListener implements Listener {
  private CMI plugin;

  public PlayTimeListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveExtendedRnage(PlayerQuitEvent playerQuitEvent) {
    if (this.plugin.getConfigManager().isCMIPlayTimeTracking()) {
      Player player = playerQuitEvent.getPlayer();
      if (player != null) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        if (cmiUser != null) {
          cmiUser.getCMIPlayTime().updatePlayTime(false);
          cmiUser.getCMIPlayTime().getPlayDayOfToday().setLastUpdate(null);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveExtendedRnage(PlayerJoinEvent playerJoinEvent) {
    if (this.plugin.getConfigManager().isCMIPlayTimeTracking()) {
      Player player = playerJoinEvent.getPlayer();
      if (player != null) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        if (cmiUser != null) {
          cmiUser.getCMIPlayTime().updatePlayTime(false);
          cmiUser.getCMIPlayTime().getPlayDayOfToday().setLastUpdate(System.currentTimeMillis());
        }
      }
    }
  }
}
