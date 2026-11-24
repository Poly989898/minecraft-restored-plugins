package net.Indyuce.mmoitems.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class ItemBuildEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private ItemStack itemStack;

   public ItemBuildEvent(ItemStack var1) {
      this.itemStack = var1;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public ItemBuildEvent setItemStack(ItemStack var1) {
      this.itemStack = var1;
      return this;
   }

   @Deprecated
   public boolean isCancelled() {
      return this.itemStack == null;
   }

   @Deprecated
   public void setCancelled(boolean var1) {
      if (var1) {
         this.itemStack = null;
      }
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
