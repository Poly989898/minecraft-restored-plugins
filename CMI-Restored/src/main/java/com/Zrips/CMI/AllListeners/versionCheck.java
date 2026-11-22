package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class versionCheck implements Listener {
  private CMI plugin;

  public versionCheck(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onJoin(PlayerJoinEvent playerJoinEvent) {
    if (this.plugin.getConfigManager().isShowNewVersion()
        && PermissionsManager.CMIPerm.versioncheck.hasPermission(playerJoinEvent.getPlayer())) {
      this.plugin.getVersionCheckManager().VersionCheck(playerJoinEvent.getPlayer());
    }
  }
}
