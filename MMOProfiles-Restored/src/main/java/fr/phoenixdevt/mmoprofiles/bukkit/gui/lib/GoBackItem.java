package fr.phoenixdevt.mmoprofiles.bukkit.gui.lib;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GoBackItem extends InventoryItem<GeneratedInventory> {
   public GoBackItem(ConfigurationSection config) {
      super(config);
   }

   @Override
   public void onClick(GeneratedInventory inv, InventoryClickEvent event) {
      inv.getNavigator().popOpen();
   }
}
