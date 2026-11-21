package it.ytnoos.lpx.listeners;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.utils.TimeUtils;
import it.ytnoos.lpx.utils.BlockStateHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {
   private final LPXBukkit lpxBukkit;

   public InventoryListener(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void interactContainer(PlayerInteractEvent playerInteractEvent) {
      if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK
         && BlockStateHandler.getBlockState(playerInteractEvent.getClickedBlock()) instanceof InventoryHolder
         && this.lpxBukkit.getConfigManager().getOptionsConfig().isInteractContainerDelayEnabled()) {
         ProtocolPlayer protocolPlayer = this.lpxBukkit.getRegistry().getPlayer(playerInteractEvent.getPlayer()).orElse(null);
         if (protocolPlayer != null && !protocolPlayer.getPrinterData().isUsingPrinter()) {
            if (TimeUtils.difference(protocolPlayer.getLastContainerInteract()) < this.lpxBukkit.getConfigManager().getOptionsConfig().getInteractContainerDelay()) {
               playerInteractEvent.setUseInteractedBlock(Result.DENY);
            } else {
               if (!playerInteractEvent.isCancelled()) {
                  protocolPlayer.updateLastContainerInteract();
               }
            }
         }
      }
   }

}
