package io.lumine.mythic.lib.version;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface VInventoryView {
   String getTitle();

   InventoryType getType();

   Inventory getTopInventory();

   Inventory getBottomInventory();

   void setCursor(ItemStack var1);

   HumanEntity getPlayer();

   void close();
}
