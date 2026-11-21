package it.ytnoos.lpx.inventories.smartinvs.opener;

import com.google.common.collect.ImmutableList;
import it.ytnoos.lpx.inventories.smartinvs.InventoryManager;
import it.ytnoos.lpx.inventories.smartinvs.SmartInventory;
import it.ytnoos.lpx.inventories.smartinvs.content.InventoryContents;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class SpecialInventoryOpener implements InventoryOpener {
   private static final List<InventoryType> SUPPORTED = ImmutableList.of(
      InventoryType.FURNACE,
      InventoryType.WORKBENCH,
      InventoryType.DISPENSER,
      InventoryType.DROPPER,
      InventoryType.ENCHANTING,
      InventoryType.BREWING,
      InventoryType.ANVIL,
      InventoryType.BEACON,
      InventoryType.HOPPER
   );

   public SpecialInventoryOpener() {
   }

   @Override
   public Inventory open(SmartInventory smartInventory, Player player) {
      InventoryManager inventoryManager = smartInventory.getInventoryManager();
      Inventory inventory = Bukkit.createInventory(player, smartInventory.getType(), smartInventory.getTitle());
      this.i(inventory, (InventoryContents)inventoryManager.getContents(player).get());
      player.openInventory(inventory);
      return inventory;
   }

   @Override
   public boolean supports(InventoryType inventoryType) {
      return SUPPORTED.contains(inventoryType);
   }

}
