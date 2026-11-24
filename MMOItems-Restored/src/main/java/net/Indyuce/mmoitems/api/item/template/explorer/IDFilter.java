package net.Indyuce.mmoitems.api.item.template.explorer;

import java.util.function.Predicate;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;

public class IDFilter implements Predicate<MMOItemTemplate> {
   private final String id;

   public IDFilter(String text) {
      this.id = text;
   }

   public boolean test(MMOItemTemplate mmoItemTemplate) {
      return mmoItemTemplate.getId().equalsIgnoreCase(this.id);
   }
}
