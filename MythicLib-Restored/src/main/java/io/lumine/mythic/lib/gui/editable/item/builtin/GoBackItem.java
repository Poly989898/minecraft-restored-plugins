package io.lumine.mythic.lib.gui.editable.item.builtin;

import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

/** Goes back to the GUI the player came from. */
public class GoBackItem<T extends GeneratedInventory> extends ClickSoundItem<T> {
   public GoBackItem(ConfigurationSection config) {
      super(config);
   }

   @Override
   public void onClick(T inv, InventoryClickEvent event) {
      this.playClickSound(inv);
      inv.getNavigator().unblockClosing();
      inv.getNavigator().popOpen();
   }
}
