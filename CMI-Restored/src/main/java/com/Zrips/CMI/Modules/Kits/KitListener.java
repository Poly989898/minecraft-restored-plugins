package com.Zrips.CMI.Modules.Kits;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class KitListener implements Listener {
  private CMI plugin;

  public KitListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerFirstLogin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (!player.hasPlayedBefore()) {
      Kit kit = this.plugin.getKitsManager().getNewbieKit();
      if (kit != null) {
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (playerJoinEvent.getPlayer().isOnline()) {
                this.plugin.getKitsManager().giveKit(player, kit);
              }
            },
            20L);
      }
    }
  }
}
