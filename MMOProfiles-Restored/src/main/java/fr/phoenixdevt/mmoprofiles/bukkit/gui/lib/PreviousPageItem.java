package fr.phoenixdevt.mmoprofiles.bukkit.gui.lib;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PreviousPageItem extends InventoryItem<GeneratedInventory> {
   public PreviousPageItem(ConfigurationSection config) {
      super(config);
   }

   @Override
   public ItemStack getDisplayedItem(GeneratedInventory inv, int n) {
      return inv.page <= 1 ? null : super.getDisplayedItem(inv, n);
   }

   @Override
   public void onClick(GeneratedInventory inv, InventoryClickEvent event) {
      if (inv.page > 1) {
         inv.page--;
         inv.open();
      }
   }
}
