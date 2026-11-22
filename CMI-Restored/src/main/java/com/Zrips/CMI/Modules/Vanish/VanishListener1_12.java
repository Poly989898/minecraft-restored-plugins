package com.Zrips.CMI.Modules.Vanish;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class VanishListener1_12 implements Listener {
  private CMI plugin;

  public VanishListener1_12(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onBlockReceiveGameEvent(PlayerAdvancementDoneEvent playerAdvancementDoneEvent) {
    if (playerAdvancementDoneEvent.getPlayer() instanceof Player) {
      if (!this.plugin.getVanishManager().getAllVanished().isEmpty()) {
        if (CMIUser.getUser(playerAdvancementDoneEvent.getPlayer()).isVanished()) {
          playerAdvancementDoneEvent.message(null);
        }
      }
    }
  }
}
