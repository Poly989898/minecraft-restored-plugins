package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.placeholders.types.EntityPlaceholder;
import java.util.function.BiFunction;

@Deprecated
public class FunctionalEntityPlaceholder implements EntityPlaceholder {
   private BiFunction<AbstractEntity, String, String> transformer;

   public FunctionalEntityPlaceholder() {
   }

   public FunctionalEntityPlaceholder(BiFunction<AbstractEntity, String, String> transformer) {
      this.transformer = transformer;
   }

   public String apply(AbstractEntity entity, String conditional) {
      return this.transformer.apply(entity, conditional);
   }
}
