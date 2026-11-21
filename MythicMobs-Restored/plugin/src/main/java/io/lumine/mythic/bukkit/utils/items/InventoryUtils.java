package io.lumine.mythic.bukkit.utils.items;

import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
   public static boolean containsItem(Player player, Material material, int amount) {
      return containsItem(player.getInventory(), new ItemStack(material), amount);
   }

   public static boolean containsItem(Inventory inventory, Material material, int amount) {
      return containsItem(inventory, new ItemStack(material), amount);
   }

   public static boolean containsItem(Player player, ItemStack item, int amount) {
      return containsItem(player.getInventory(), item, amount);
   }

   public static boolean containsItem(Inventory inventory, ItemStack item, int amount) {
      int found = 0;

      for (ItemStack i : inventory.getContents()) {
         if (item.isSimilar(i)) {
            found += i.getAmount();
         }
      }

      return found >= amount;
   }

   public static int removeItems(Player player, Material material, int amount) {
      return removeItems(player.getInventory(), new ItemStack(material), amount);
   }

   public static int removeItems(Inventory inventory, Material material, int amount) {
      return removeItems(inventory, new ItemStack(material), amount);
   }

   public static int removeItems(Player player, ItemStack item, int amount) {
      return removeItems(player.getInventory(), item, amount);
   }

   public static int removeItems(Inventory inventory, ItemStack item, int amount) {
      for (ItemStack stack : inventory) {
         if (item.isSimilar(stack)) {
            int stackAmount = stack.getAmount();
            if (stackAmount > amount) {
               stack.setAmount(stackAmount - amount);
            } else {
               stack.setAmount(0);
            }

            amount -= stackAmount;
            if (amount <= 0) {
               break;
            }
         }
      }

      return amount;
   }

   public static int count(Inventory inventory, Predicate<ItemStack> filter) {
      return inventory != null ? count(inventory.getStorageContents(), filter) : 0;
   }

   public static int count(ItemStack[] contents, Predicate<ItemStack> filter) {
      if (contents.length == 0) {
         return 0;
      } else {
         int amount = 0;

         for (ItemStack item : contents) {
            if (ItemUtils.isValid(item) && filter.test(item)) {
               amount += item.getAmount();
            }
         }

         return amount;
      }
   }

   public static boolean contains(Inventory inventory, int amount, Predicate<ItemStack> filter) {
      return inventory != null && contains(inventory.getStorageContents(), amount, filter);
   }

   public static boolean contains(ItemStack[] contents, int amount, Predicate<ItemStack> filter) {
      if (contents.length == 0) {
         return false;
      } else if (amount <= 0) {
         return true;
      } else {
         for (ItemStack item : contents) {
            if (ItemUtils.isValid(item) && filter.test(item)) {
               amount -= item.getAmount();
               if (amount <= 0) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static int remove(Inventory inventory, int amount, Predicate<ItemStack> filter) {
      return inventory != null ? remove(inventory.getStorageContents(), amount, filter) : 0;
   }

   public static int remove(ItemStack[] contents, int amount, Predicate<ItemStack> filter) {
      if (contents.length != 0 && amount > 0) {
         int maximum = amount;

         for (ItemStack item : contents) {
            if (amount <= 0) {
               return maximum;
            }

            if (ItemUtils.isValid(item) && filter.test(item)) {
               int count = item.getAmount();
               if (count >= amount) {
                  item.subtract(amount);
                  amount = 0;
                  break;
               }

               item.subtract(count);
               amount -= count;
            }
         }

         return maximum - amount;
      } else {
         return 0;
      }
   }

   public static int emptySlots(Inventory inventory) {
      if (inventory == null) {
         throw new IllegalArgumentException("Inventory cannot be null");
      } else {
         int emptySlots = 0;

         for (ItemStack item : inventory.getContents()) {
            if (item == null) {
               emptySlots++;
            }
         }

         return emptySlots;
      }
   }

   private InventoryUtils() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
