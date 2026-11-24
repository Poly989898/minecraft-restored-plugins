package net.Indyuce.mmoitems.api.event;

import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class CraftMMOItemEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();
   private ItemStack result;

   public CraftMMOItemEvent(PlayerData var1, ItemStack var2) {
      super(var1);
      this.result = var2;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public void setResult(ItemStack var1) {
      this.result = var1;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
