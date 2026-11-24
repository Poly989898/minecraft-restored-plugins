package net.Indyuce.mmocore.api.event;

import java.util.List;
import net.Indyuce.mmocore.api.block.BlockInfo;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class CustomBlockMineEvent extends PlayerDataEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Block block;
   private final BlockInfo info;
   private List<ItemStack> drops;
   private boolean cancelled;

   public CustomBlockMineEvent(PlayerData player, Block block, BlockInfo info, List<ItemStack> drops, boolean cancelled) {
      super(player);
      this.block = block;
      this.info = info;
      this.drops = drops;
      this.cancelled = cancelled;
   }

   public Block getBlock() {
      return this.block;
   }

   public List<ItemStack> getDrops() {
      return this.drops;
   }

   public BlockInfo getBlockInfo() {
      return this.info;
   }

   public void setDrops(List<ItemStack> list) {
      this.drops = list;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean value) {
      this.cancelled = value;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
