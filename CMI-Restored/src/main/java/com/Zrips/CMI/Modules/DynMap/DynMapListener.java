package com.Zrips.CMI.Modules.DynMap;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.events.CMIPlayerUnVanishEvent;
import com.Zrips.CMI.events.CMIPlayerVanishEvent;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class DynMapListener implements Listener {
  private CMI plugin;

  public DynMapListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void CMIPlayerUnVanishEvent(CMIPlayerUnVanishEvent cmiPlayerUnVanishEvent) {
    this.plugin.getDynMapManager().processPlayer(cmiPlayerUnVanishEvent.getPlayer());
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void CMIPlayerVanishEvent(CMIPlayerVanishEvent cmiPlayerVanishEvent) {
    this.plugin.getDynMapManager().processPlayer(cmiPlayerVanishEvent.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerLoginEvent(PlayerLoginEvent playerLoginEvent) {
    CMIScheduler.runTaskLater(
        this.plugin,
        () -> this.plugin.getDynMapManager().processPlayer(playerLoginEvent.getPlayer()),
        5L);
  }
}
