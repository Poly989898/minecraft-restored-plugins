package com.nexomc.nexo.items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UpdateCallback {
   default @Nullable ItemStack preUpdate(ItemStack itemStack) {
      return itemStack;
   }

   default @NotNull ItemStack postUpdate(String itemId, ItemStack itemStack, ItemStack preUpdateItemStack) {
      return itemStack;
   }
}
