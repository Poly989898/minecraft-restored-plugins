package io.lumine.mythic.bukkit.utils.items;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {
   public static final ItemStack[] EMPTY_ITEM_ARRAY = new ItemStack[0];
   public static final List<EquipmentSlot> HANDS = List.of(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);

   public static boolean isEmpty(ItemStack item) {
      return item == null || item.getType() == Material.AIR || item.getAmount() <= 0;
   }

   public static boolean isValid(ItemStack item) {
      return item != null && item.getType() != Material.AIR && item.getAmount() > 0;
   }
}
