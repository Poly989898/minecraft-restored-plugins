package it.ytnoos.lpx;

import it.ytnoos.lpx.api.adapter.InventoryViewAdapter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class LegacyInventoryViewAdapter implements InventoryViewAdapter {
   private final InventoryView inventoryView;

   public LegacyInventoryViewAdapter(Player player) {
      this.inventoryView = player.getOpenInventory();
   }

   @Override
   public boolean isNull() {
      return this.inventoryView == null;
   }

   @Override
   public String getType() {
      return this.inventoryView.getType().name();
   }

   @Override
   public Inventory getTopInventory() {
      return this.inventoryView.getTopInventory();
   }

   @Override
   public Inventory getBottomInventory() {
      return this.inventoryView.getBottomInventory();
   }

   @Override
   public int countSlots() {
      return this.inventoryView.countSlots();
   }

   @Override
   public int convertSlot(int value) {
      return this.inventoryView.convertSlot(value);
   }

}
