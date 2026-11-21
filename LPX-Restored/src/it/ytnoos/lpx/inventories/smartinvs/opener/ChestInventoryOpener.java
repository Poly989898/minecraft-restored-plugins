package it.ytnoos.lpx.inventories.smartinvs.opener;

import it.ytnoos.lpx.inventories.smartinvs.InventoryManager;
import it.ytnoos.lpx.inventories.smartinvs.SmartInventory;
import it.ytnoos.lpx.inventories.smartinvs.content.InventoryContents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class ChestInventoryOpener implements InventoryOpener {
   public ChestInventoryOpener() {
   }

   @Override
   public Inventory open(SmartInventory smartInventory, Player player) {
      InventoryManager inventoryManager = smartInventory.getInventoryManager();
      Inventory inventory = Bukkit.createInventory(player, smartInventory.getRows() * smartInventory.getColumns(), smartInventory.getTitle());
      this.i(inventory, (InventoryContents)inventoryManager.getContents(player).get());
      player.openInventory(inventory);
      return inventory;
   }

   @Override
   public boolean supports(InventoryType inventoryType) {
      long longValue = 42248571798425L;
      InventoryType inventoryType2 = inventoryType;
      if (longValue > 1L) {
         if (inventoryType == InventoryType.CHEST) {
            return true;
         }

         inventoryType2 = inventoryType;
      }

      return inventoryType2 == InventoryType.ENDER_CHEST;
   }

}
