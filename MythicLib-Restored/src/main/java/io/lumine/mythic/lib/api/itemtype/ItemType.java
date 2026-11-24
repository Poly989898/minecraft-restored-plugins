package io.lumine.mythic.lib.api.itemtype;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ItemType {
   boolean matches(ItemStack var1);

   String display();

   @Override
   int hashCode();

   @Override
   boolean equals(Object var1);

   static ItemType fromString(String input) {
      if (!input.contains(".") && !input.contains("%") && !input.contains("?")) {
         return new VanillaType(Material.valueOf(UtilityMethods.enumName(input)));
      }

      String[] split = input.split("[.%?]");
      Validate.isTrue(split.length == 2, "Please specify a type and ID");
      return new MMOItemType(split[0], split[1]);
   }

   static ItemType fromItemStack(ItemStack item) {
      NBTItem nbtItem = NBTItem.get(item);
      return nbtItem.hasTag("MMOITEMS_ITEM_TYPE")
         ? new MMOItemType(nbtItem.getString("MMOITEMS_ITEM_TYPE"), nbtItem.getString("MMOITEMS_ITEM_ID"))
         : new VanillaType(item.getType());
   }
}
