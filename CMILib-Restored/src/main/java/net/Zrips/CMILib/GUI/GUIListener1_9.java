package net.Zrips.CMILib.GUI;

import net.Zrips.CMILib.CMILib;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class GUIListener1_9 implements Listener {
  CMILib plugin;

  public GUIListener1_9(CMILib plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void playerInteractEvent(PlayerSwapHandItemsEvent event) {
    if (!event.isCancelled()) {
      Player player = event.getPlayer();
      if (this.plugin.getGUIManager().isOpenedGui(player)) {
        event.setCancelled(true);
      }
    }
  }
}
