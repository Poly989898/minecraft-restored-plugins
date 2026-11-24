package net.Indyuce.mmoitems.api.item.template.explorer;

import java.util.function.Predicate;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;

public class TierFilter implements Predicate<MMOItemTemplate> {
   private final String id;

   public TierFilter(String text) {
      this.id = text;
   }

   public boolean test(MMOItemTemplate mmoItemTemplate) {
      RandomStatData randomStatData = mmoItemTemplate.getBaseItemData().get(ItemStats.TIER);
      return randomStatData != null && randomStatData.toString().equalsIgnoreCase(this.id);
   }
}
