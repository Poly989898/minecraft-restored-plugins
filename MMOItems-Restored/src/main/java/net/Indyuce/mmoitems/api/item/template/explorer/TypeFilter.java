package net.Indyuce.mmoitems.api.item.template.explorer;

import java.util.function.Predicate;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;

public class TypeFilter implements Predicate<MMOItemTemplate> {
   private final Type type;

   public TypeFilter(Type type) {
      this.type = type;
   }

   public boolean test(MMOItemTemplate mmoItemTemplate) {
      return mmoItemTemplate.getType().equals(this.type);
   }
}
