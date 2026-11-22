package com.Zrips.CMI.Modules.Vanish;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockReceiveGameEvent;

public class VanishListener1_19 implements Listener {
  private CMI plugin;

  public VanishListener1_19(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onBlockReceiveGameEvent(BlockReceiveGameEvent blockReceiveGameEvent) {
    if (blockReceiveGameEvent.getEntity() instanceof Player) {
      if (blockReceiveGameEvent.getBlock().getType().equals(Material.SCULK_SENSOR)) {
        if (!this.plugin.getVanishManager().getAllVanished().isEmpty()) {
          if (CMIUser.getUser((Player) blockReceiveGameEvent.getEntity()).isVanished()) {
            blockReceiveGameEvent.setCancelled(true);
          }
        }
      }
    }
  }
}
