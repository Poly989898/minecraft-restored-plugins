package com.ticxo.modelengine.core.menu.widget;

import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.menu.Widget;
import com.ticxo.modelengine.api.utils.data.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BorderWidget implements Widget {
   private final ItemStack border = new ItemStack(Material.TWISTING_VINES);
   private final ItemStack bar;

   public BorderWidget() {
      ItemUtils.name(this.border, Component.text(" "));
      this.bar = new ItemStack(Material.NETHER_SPROUTS);
      ItemUtils.name(this.bar, Component.text(" "));
   }

   @Override
   public ItemStack getItemForSlot(int size, int slot) {
      if (slot % 9 == 0 || slot % 9 == 8) {
         return this.border;
      } else {
         return (slot < 9 || slot >= 18) && slot <= size - 9 ? null : this.bar;
      }
   }

   @Override
   public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
   }
}
