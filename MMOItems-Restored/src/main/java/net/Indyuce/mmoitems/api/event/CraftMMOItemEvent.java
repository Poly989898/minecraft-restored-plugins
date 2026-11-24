package net.Indyuce.mmoitems.api.event;

import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class CraftMMOItemEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();
   private ItemStack result;

   public CraftMMOItemEvent(PlayerData playerData, ItemStack item) {
      super(playerData);
      this.result = item;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public void setResult(ItemStack item) {
      this.result = item;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
