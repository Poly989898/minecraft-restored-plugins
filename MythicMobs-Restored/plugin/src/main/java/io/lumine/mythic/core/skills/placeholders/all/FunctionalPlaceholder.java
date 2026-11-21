package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.types.GeneralPlaceholder;
import java.util.function.Function;

@Deprecated
public class FunctionalPlaceholder implements GeneralPlaceholder {
   private final Function<String, String> transformer;

   public FunctionalPlaceholder(Function<String, String> transformer) {
      this.transformer = transformer;
   }

   public String apply(String conditional) {
      return this.transformer.apply(conditional);
   }
}
