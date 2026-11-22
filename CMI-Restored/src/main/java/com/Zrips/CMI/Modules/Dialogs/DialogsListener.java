package com.Zrips.CMI.Modules.Dialogs;

import com.Zrips.CMI.CMI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DialogsListener implements Listener {
  private CMI plugin;

  public DialogsListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void PlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    this.plugin.getDialogManager().clearCache(playerQuitEvent.getPlayer().getUniqueId());
  }
}
