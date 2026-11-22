package com.Zrips.CMI.Modules.Packets;

import com.Zrips.CMI.CMI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PacketPlayerListeners implements Listener {
  private CMI plugin;

  public PacketPlayerListeners(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPacketPlayerLogin(PlayerJoinEvent playerJoinEvent) {}

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPacketPlayerQuit(PlayerQuitEvent playerQuitEvent) {
    this.plugin.getPacketInjector().removePlayer(playerQuitEvent.getPlayer());
  }
}
