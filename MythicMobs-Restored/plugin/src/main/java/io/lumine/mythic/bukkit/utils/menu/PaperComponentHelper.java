package io.lumine.mythic.bukkit.utils.menu;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public final class PaperComponentHelper {
   public static void setItemModelComponent(ItemStack item, String value) {
      item.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey.fromString(value));
   }

   public static void setItemModelComponent(ItemStack item, NamespacedKey value) {
      item.setData(DataComponentTypes.ITEM_MODEL, value);
   }

   public static void setTooltipStyleComponent(ItemStack item, NamespacedKey value) {
      item.setData(DataComponentTypes.TOOLTIP_STYLE, value);
   }
}
