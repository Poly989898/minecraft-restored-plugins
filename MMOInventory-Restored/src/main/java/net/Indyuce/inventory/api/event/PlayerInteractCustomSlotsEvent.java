package net.Indyuce.inventory.api.event;

import java.util.Collection;
import javax.annotation.Nullable;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.inventory.InventoryItem;
import net.Indyuce.inventory.inventory.InventoryLookupMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractCustomSlotsEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   protected Collection<InventoryItem> items;
   protected Action action;
   protected Block blockClicked;
   protected BlockFace blockFace;
   private Result useClickedBlock;
   private Result useItemInHand;
   private InventoryHandler inventoryHandler;

   public PlayerInteractCustomSlotsEvent(@NotNull InventoryHandler who, @NotNull Action action, @Nullable Block clickedBlock, @NotNull BlockFace clickedFace) {
      super(who.getPlayer());
      this.inventoryHandler = who;
      this.action = action;
      this.items = who.getItems(InventoryLookupMode.NORMAL);
      this.blockClicked = clickedBlock;
      this.blockFace = clickedFace;
      this.useItemInHand = Result.DEFAULT;
      this.useClickedBlock = clickedBlock == null ? Result.DENY : Result.ALLOW;
   }

   @NotNull
   public Action getAction() {
      return this.action;
   }

   public InventoryHandler getInventoryHandler() {
      return this.inventoryHandler;
   }

   @Deprecated
   public boolean isCancelled() {
      return this.useInteractedBlock() == Result.DENY;
   }

   public void setCancelled(boolean cancel) {
      this.setUseInteractedBlock(cancel ? Result.DENY : (this.useInteractedBlock() == Result.DENY ? Result.DEFAULT : this.useInteractedBlock()));
      this.setUseItemInHand(cancel ? Result.DENY : (this.useItemInHand() == Result.DENY ? Result.DEFAULT : this.useItemInHand()));
   }

   @Nullable
   public Collection<InventoryItem> getItems() {
      return this.items;
   }

   public boolean hasBlock() {
      return this.blockClicked != null;
   }

   @Nullable
   public Block getClickedBlock() {
      return this.blockClicked;
   }

   @NotNull
   public BlockFace getBlockFace() {
      return this.blockFace;
   }

   @NotNull
   public Result useInteractedBlock() {
      return this.useClickedBlock;
   }

   public void setUseInteractedBlock(@NotNull Result useInteractedBlock) {
      this.useClickedBlock = useInteractedBlock;
   }

   @NotNull
   public Result useItemInHand() {
      return this.useItemInHand;
   }

   public void setUseItemInHand(@NotNull Result useItemInHand) {
      this.useItemInHand = useItemInHand;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   @NotNull
   public static HandlerList getHandlerList() {
      return handlers;
   }
}
