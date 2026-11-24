package net.Indyuce.mmoitems.api.item.template.explorer;

import java.util.function.Predicate;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;

public class TierFilter implements Predicate<MMOItemTemplate> {
   private final String id;

   public TierFilter(String var1) {
      this.id = var1;
   }

   public boolean test(MMOItemTemplate var1) {
      RandomStatData var2 = var1.getBaseItemData().get(ItemStats.TIER);
      return var2 != null && var2.toString().equalsIgnoreCase(this.id);
   }
}
