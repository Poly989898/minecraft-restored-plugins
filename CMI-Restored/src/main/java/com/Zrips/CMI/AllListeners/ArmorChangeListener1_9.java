package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class ArmorChangeListener1_9 implements Listener {
  private CMI plugin;

  public ArmorChangeListener1_9(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler
  public void playerInteractEvent(PlayerSwapHandItemsEvent playerSwapHandItemsEvent) {
    ArmorChangeListener.checked.remove(playerSwapHandItemsEvent.getPlayer().getUniqueId());
  }
}
