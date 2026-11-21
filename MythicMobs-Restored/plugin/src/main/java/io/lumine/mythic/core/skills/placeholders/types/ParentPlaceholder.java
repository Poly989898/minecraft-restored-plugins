package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.function.BiFunction;

@Deprecated
public class ParentPlaceholder implements Placeholder, BiFunction<PlaceholderMeta, String, String> {
   private final BiFunction<AbstractEntity, String, String> transformer;

   public ParentPlaceholder(BiFunction<AbstractEntity, String, String> transformer) {
      this.transformer = transformer;
   }

   public String apply(PlaceholderMeta target, String conditional) {
      if (target.getCaster() instanceof ActiveMob am) {
         return am.getParent().isPresent() ? this.transformer.apply(am.getParent().get(), conditional) : null;
      } else {
         return null;
      }
   }
}
