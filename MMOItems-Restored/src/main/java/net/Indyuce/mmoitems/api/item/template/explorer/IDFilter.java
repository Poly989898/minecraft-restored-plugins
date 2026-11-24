package net.Indyuce.mmoitems.api.item.template.explorer;

import java.util.function.Predicate;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;

public class IDFilter implements Predicate<MMOItemTemplate> {
   private final String id;

   public IDFilter(String var1) {
      this.id = var1;
   }

   public boolean test(MMOItemTemplate var1) {
      return var1.getId().equalsIgnoreCase(this.id);
   }
}
