package io.lumine.mythic.lib.gui.editable.item.builtin;

import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

/** Closes the inventory. */
public class CloseInventoryItem<T extends GeneratedInventory> extends ClickSoundItem<T> {
   public CloseInventoryItem(ConfigurationSection config) {
      super(config);
   }

   @Override
   public void onClick(T inv, InventoryClickEvent event) {
      this.playClickSound(inv);
      inv.getPlayer().closeInventory();
   }
}
