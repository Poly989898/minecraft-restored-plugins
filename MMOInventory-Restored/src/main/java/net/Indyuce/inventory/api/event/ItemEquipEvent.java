package net.Indyuce.inventory.api.event;

import javax.annotation.Nullable;
import net.Indyuce.inventory.slot.CustomSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class ItemEquipEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   @Nullable
   private final ItemStack item;
   private final CustomSlot slot;
   private final ItemEquipEvent.EquipAction action;
   private boolean cancelled = false;

   public ItemEquipEvent(Player player, @Nullable ItemStack item, CustomSlot slot, ItemEquipEvent.EquipAction action) {
      super(player);
      this.item = item;
      this.action = action;
      this.slot = slot;
   }

   @Nullable
   public ItemStack getItem() {
      return this.item;
   }

   public boolean hasSlot() {
      return this.slot != null;
   }

   @Nullable
   public CustomSlot getSlot() {
      return this.slot;
   }

   public ItemEquipEvent.EquipAction getAction() {
      return this.action;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean bool) {
      this.cancelled = bool;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public enum EquipAction {
      EQUIP,
      UNEQUIP,
      SWAP_ITEMS,
      SHIFT_CLICK_EQUIP,
      SHIFT_CLICK_UNEQUIP;
   }
}
