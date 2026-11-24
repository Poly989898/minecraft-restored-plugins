package net.Indyuce.mmoitems.manager;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.inventory.ItemStack;

public class ItemManager {
   @Deprecated
   public MMOItem getMMOItem(Type var1, String var2) {
      return MMOItems.plugin.getMMOItem(var1, var2);
   }

   @Deprecated
   public ItemStack getItem(Type var1, String var2) {
      return MMOItems.plugin.getItem(var1, var2);
   }
}
