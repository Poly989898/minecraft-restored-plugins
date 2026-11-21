package com.ticxo.modelengine.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface Widget {
   static int toSlot(int x, int y) {
      return y * 9 + x;
   }

   ItemStack getItemForSlot(int size, int slot);

   void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event);
}
