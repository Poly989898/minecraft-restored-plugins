package net.Indyuce.inventory.inventory;

import net.Indyuce.inventory.slot.CustomSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryItem {
   private final ItemStack stack;
   private final CustomSlot slot;

   public InventoryItem(@NotNull ItemStack stack, @NotNull CustomSlot slot) {
      this.stack = stack;
      this.slot = slot;
   }

   @NotNull
   public ItemStack getItemStack() {
      return this.stack;
   }

   @NotNull
   public CustomSlot getSlot() {
      return this.slot;
   }
}
