package net.Indyuce.mmoitems.manager;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.inventory.ItemStack;

public class ItemManager {
   @Deprecated
   public MMOItem getMMOItem(Type type, String text) {
      return MMOItems.plugin.getMMOItem(type, text);
   }

   @Deprecated
   public ItemStack getItem(Type type, String text) {
      return MMOItems.plugin.getItem(type, text);
   }
}
