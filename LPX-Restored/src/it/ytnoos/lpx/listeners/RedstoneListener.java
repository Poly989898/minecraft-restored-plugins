package it.ytnoos.lpx.listeners;

import it.ytnoos.lpx.LPXBukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneListener implements Listener {
   private final LPXBukkit lpxBukkit;
   private final BlockFace[] faces = BlockFace.values();

   public RedstoneListener(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
   }

   @EventHandler
   public void onRedstoneEvent(BlockRedstoneEvent blockRedstoneEvent) {
      if (this.lpxBukkit.getConfigManager().getOptionsConfig().isTrapdoorRailRedstone()) {
         Block block = blockRedstoneEvent.getBlock();
         String string = block.getType().name();
         if (string.contains("TRAP") && string.contains("DOOR") && this.isNearRail(block)) {
            blockRedstoneEvent.setNewCurrent(blockRedstoneEvent.getOldCurrent());
         }
      }
   }

   private boolean isNearRail(Block block) {
      for (BlockFace blockFace : this.faces) {
         if (block.getRelative(blockFace).getType().name().contains("RAIL")) {
            return true;
         }
      }

      return false;
   }

}
