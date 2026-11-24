package net.Indyuce.mmocore.api.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class CustomPlayerFishEvent extends PlayerDataEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private ItemStack caught;
   private final Item droppedItem;
   private boolean cancelled = false;

   public CustomPlayerFishEvent(PlayerData player, ItemStack caught, Item droppedItem) {
      super(player);
      this.caught = caught;
      this.droppedItem = droppedItem;
   }

   public Item getDroppedItem() {
      return this.droppedItem;
   }

   public ItemStack getCaught() {
      return this.caught;
   }

   public void setCaught(ItemStack caught) {
      this.caught = caught;
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
