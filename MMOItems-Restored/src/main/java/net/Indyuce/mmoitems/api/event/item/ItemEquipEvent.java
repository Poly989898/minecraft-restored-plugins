package net.Indyuce.mmoitems.api.event.item;

import net.Indyuce.mmoitems.api.event.PlayerDataEvent;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemEquipEvent extends PlayerDataEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final ItemStack item;

   public ItemEquipEvent(Player var1, ItemStack var2) {
      super(PlayerData.get(var1));
      this.item = var2;
   }

   public ItemStack getItem() {
      return this.item;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
