package net.Indyuce.mmoitems.api.item.template.explorer;

import java.util.function.Predicate;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.data.StringListData;

public class ClassFilter implements Predicate<MMOItemTemplate> {
   private final String name;

   public ClassFilter(RPGPlayer var1) {
      this(var1.getClassName());
   }

   public ClassFilter(String var1) {
      this.name = var1;
   }

   public boolean test(MMOItemTemplate var1) {
      if (!var1.getBaseItemData().containsKey(ItemStats.REQUIRED_CLASS)) {
         return true;
      }

      for (String var3 : ((StringListData)var1.getBaseItemData().get(ItemStats.REQUIRED_CLASS)).getList()) {
         if (var3.equalsIgnoreCase(this.name)) {
            return true;
         }
      }

      return false;
   }
}
