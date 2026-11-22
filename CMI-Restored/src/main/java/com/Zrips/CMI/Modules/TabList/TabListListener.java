package com.Zrips.CMI.Modules.TabList;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.events.CMIAfkEnterEvent;
import com.Zrips.CMI.events.CMIAfkLeaveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TabListListener implements Listener {
  private CMI plugin;

  public TabListListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (player != null
        && this.plugin.getTabListManager().isEnabled()
        && this.plugin.getTabListManager().isUpdatesOnJoin()) {
      this.plugin.getTabListManager().addPlayerData(player);
      this.plugin.getTabListManager().updateTablistName(player);
      this.plugin.getTabListManager().updateTabList(3);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    if (player != null
        && this.plugin.getTabListManager().isEnabled()
        && this.plugin.getTabListManager().isUpdatesOnLeave()) {
      this.plugin.getTabListManager().updateTabList(3);
      this.plugin.getTabListManager().removePlayerData(player);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent playerChangedWorldEvent) {
    Player player = playerChangedWorldEvent.getPlayer();
    if (player != null
        && this.plugin.getTabListManager().isEnabled()
        && this.plugin.getTabListManager().isUpdatesOnWorldChange()) {
      this.plugin.getTabListManager().updateTabList(3);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeathEvent(PlayerDeathEvent playerDeathEvent) {
    Player player = playerDeathEvent.getEntity();
    if (player != null
        && this.plugin.getTabListManager().isEnabled()
        && this.plugin.getTabListManager().isUpdatesOnPlayerDeath()) {
      this.plugin.getTabListManager().updateTabList(3);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCMIAfkLeaveEvent(CMIAfkLeaveEvent cmiAfkLeaveEvent) {
    Player player = cmiAfkLeaveEvent.getPlayer();
    if (player != null
        && this.plugin.getTabListManager().isEnabled()
        && this.plugin.getTabListManager().isUpdatesOnAfkStateChange()) {
      this.plugin.getTabListManager().updateTabList(3);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCMIAfkEnterEvent(CMIAfkEnterEvent cmiAfkEnterEvent) {
    Player player = cmiAfkEnterEvent.getPlayer();
    if (player != null
        && this.plugin.getTabListManager().isEnabled()
        && this.plugin.getTabListManager().isUpdatesOnAfkStateChange()) {
      this.plugin.getTabListManager().updateTabList(3);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerTeleportEvent(PlayerTeleportEvent playerTeleportEvent) {
    Player player = playerTeleportEvent.getPlayer();
    if (player != null
        && this.plugin.getTabListManager().isEnabled()
        && this.plugin.getTabListManager().isUpdatesOnPlayerTeleport()) {
      this.plugin.getTabListManager().updateTabList(3);
    }
  }
}
