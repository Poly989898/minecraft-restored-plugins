package fr.phoenixdevt.mmoprofiles.bukkit.gui.lib;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class NextPageItem extends InventoryItem<GeneratedInventory> {
   public NextPageItem(ConfigurationSection config) {
      super(config);
   }

   @Override
   public ItemStack getDisplayedItem(GeneratedInventory inv, int n) {
      return inv.page >= inv.getMaxPage() ? null : super.getDisplayedItem(inv, n);
   }

   @Override
   public void onClick(GeneratedInventory inv, InventoryClickEvent event) {
      if (inv.page < inv.getMaxPage()) {
         inv.page++;
         inv.open();
      }
   }
}
