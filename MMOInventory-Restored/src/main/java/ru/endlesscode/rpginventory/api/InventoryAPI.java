package ru.endlesscode.rpginventory.api;

import java.util.ArrayList;
import java.util.List;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.gui.PlayerInventoryView;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.inventory.InventoryItem;
import net.Indyuce.inventory.inventory.InventoryLookupMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryAPI {
   public static boolean isRPGInventory(@NotNull Inventory inventory) {
      InventoryHolder holder = inventory.getHolder();
      return holder != null && holder instanceof PlayerInventoryView;
   }

   @NotNull
   public static List<ItemStack> getPassiveItems(@NotNull Player player) {
      List<ItemStack> passiveItems = new ArrayList<>();
      InventoryHandler inv = (InventoryHandler)MMOInventory.plugin.getDataManager().get(player);

      for (InventoryItem invItem : inv.getItems(InventoryLookupMode.NORMAL)) {
         passiveItems.add(invItem.getItemStack());
      }

      return passiveItems;
   }

   @NotNull
   public static List<ItemStack> getActiveItems(@NotNull Player player) {
      return new ArrayList<>();
   }
}
