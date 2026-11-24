package fr.phoenixdevt.mmoprofiles.bukkit.gui.lib;

import org.bukkit.configuration.ConfigurationSection;

public class SimpleItem<T extends GeneratedInventory> extends InventoryItem<T> {
   public SimpleItem(ConfigurationSection config) {
      super(config);
   }
}
