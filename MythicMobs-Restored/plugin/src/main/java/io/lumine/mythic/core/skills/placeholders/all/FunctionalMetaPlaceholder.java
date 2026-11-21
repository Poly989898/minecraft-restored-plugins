package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;
import java.util.function.BiFunction;

@Deprecated
public class FunctionalMetaPlaceholder implements MetaPlaceholder {
   private BiFunction<PlaceholderMeta, String, String> transformer;

   public FunctionalMetaPlaceholder() {
   }

   public FunctionalMetaPlaceholder(BiFunction<PlaceholderMeta, String, String> transformer) {
      this.transformer = transformer;
   }

   public String apply(PlaceholderMeta target, String conditional) {
      return this.transformer.apply(target, conditional);
   }
}
