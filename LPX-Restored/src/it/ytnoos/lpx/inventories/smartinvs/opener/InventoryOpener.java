package it.ytnoos.lpx.inventories.smartinvs.opener;

import it.ytnoos.lpx.inventories.smartinvs.ClickableItem;
import it.ytnoos.lpx.inventories.smartinvs.SmartInventory;
import it.ytnoos.lpx.inventories.smartinvs.content.InventoryContents;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public interface InventoryOpener {
   Inventory open(SmartInventory smartInventory, Player player);

   boolean supports(InventoryType inventoryType);

   default void i(Inventory inventory, InventoryContents inventoryContents) {
      ClickableItem[][] clickableItemses = inventoryContents.getClickableItems();

      for (int value = 0; value < clickableItemses.length; value++) {
         for (int value2 = 0; value2 < clickableItemses[value].length; value2++) {
            if (clickableItemses[value][value2] != null) {
               inventory.setItem(9 * value + value2, clickableItemses[value][value2].getItem());
            }
         }
      }
   }

}
