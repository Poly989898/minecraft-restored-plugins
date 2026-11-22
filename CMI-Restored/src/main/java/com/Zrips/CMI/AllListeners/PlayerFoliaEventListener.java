package com.Zrips.CMI.AllListeners;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerFoliaEventListener implements Listener {
  @EventHandler(priority = EventPriority.HIGH)
  public void onRespawnSetLocation(PlayerPostRespawnEvent playerPostRespawnEvent) {}
}
