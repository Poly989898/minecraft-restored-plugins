package com.Zrips.CMI.Modules.Mirror;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class CMIMirrorListener implements Listener {
  private CMI plugin;

  public CMIMirrorListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void BlockPlaceEvent(BlockPlaceEvent blockPlaceEvent) {
    Player player = blockPlaceEvent.getPlayer();
    if (!blockPlaceEvent.isCancelled()) {
      ItemStack itemStack = blockPlaceEvent.getItemInHand();
      CMIScheduler.runTask(
          this.plugin,
          () ->
              this.plugin
                  .getMirrorManager()
                  .placeBlock(player, blockPlaceEvent.getBlock(), itemStack));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void BlockBreakEvent(BlockBreakEvent blockBreakEvent) {
    Player player = blockBreakEvent.getPlayer();
    if (!blockBreakEvent.isCancelled()) {
      this.plugin.getMirrorManager().breakBlock(player, blockBreakEvent.getBlock());
    }
  }
}
