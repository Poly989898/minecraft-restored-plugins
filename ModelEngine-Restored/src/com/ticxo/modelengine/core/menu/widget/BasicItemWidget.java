package com.ticxo.modelengine.core.menu.widget;

import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.menu.Widget;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class BasicItemWidget implements Widget {
   private final int slot;
   private final ItemStack stack;

   @Override
   public ItemStack getItemForSlot(int size, int slot) {
      return this.slot == slot ? this.stack : null;
   }

   @Override
   public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
   }
}
