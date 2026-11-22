package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListeners1_20 implements Listener {
  private CMI plugin;

  public PlayerListeners1_20(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {}
}
