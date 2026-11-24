package fr.phoenixdevt.mmoprofiles.bukkit.gui.lib;

import org.bukkit.configuration.ConfigurationSection;

public class PhysicalItem<T extends GeneratedInventory> extends InventoryItem<T> {
   public PhysicalItem(ConfigurationSection config) {
      super(config);
   }
}
