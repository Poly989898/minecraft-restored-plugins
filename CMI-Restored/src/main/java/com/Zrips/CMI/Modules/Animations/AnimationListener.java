package com.Zrips.CMI.Modules.Animations;

import com.Zrips.CMI.CMI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class AnimationListener implements Listener {
  private CMI plugin;

  public AnimationListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerQuitRiding(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    if (this.plugin.getAnimationManager().isRiding(player.getUniqueId())) {
      player.leaveVehicle();
    }
  }
}
