package net.Indyuce.mmoitems.api.item.template.explorer;

import java.util.function.Predicate;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;

public class TypeFilter implements Predicate<MMOItemTemplate> {
   private final Type type;

   public TypeFilter(Type var1) {
      this.type = var1;
   }

   public boolean test(MMOItemTemplate var1) {
      return var1.getType().equals(this.type);
   }
}
