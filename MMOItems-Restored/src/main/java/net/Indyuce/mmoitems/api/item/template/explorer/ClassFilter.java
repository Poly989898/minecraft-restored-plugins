package net.Indyuce.mmoitems.api.item.template.explorer;

import java.util.function.Predicate;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.data.StringListData;

public class ClassFilter implements Predicate<MMOItemTemplate> {
   private final String name;

   public ClassFilter(RPGPlayer rpgPlayer) {
      this(rpgPlayer.getClassName());
   }

   public ClassFilter(String text) {
      this.name = text;
   }

   public boolean test(MMOItemTemplate mmoItemTemplate) {
      if (!mmoItemTemplate.getBaseItemData().containsKey(ItemStats.REQUIRED_CLASS)) {
         return true;
      }

      for (String text : ((StringListData)mmoItemTemplate.getBaseItemData().get(ItemStats.REQUIRED_CLASS)).getList()) {
         if (text.equalsIgnoreCase(this.name)) {
            return true;
         }
      }

      return false;
   }
}
