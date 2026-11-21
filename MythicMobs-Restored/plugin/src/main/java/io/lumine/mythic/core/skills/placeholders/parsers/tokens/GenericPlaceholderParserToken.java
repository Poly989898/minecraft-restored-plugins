package io.lumine.mythic.core.skills.placeholders.parsers.tokens;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;

public class GenericPlaceholderParserToken extends PlaceholderParserToken<GenericPlaceholder<?>> {
   public GenericPlaceholderParserToken(GenericPlaceholder<?> value) {
      super(value);
   }

   @Override
   public boolean isStatic() {
      return false;
   }

   @Override
   public String apply(PlaceholderContext placeholderContext) {
      return this.value.apply(placeholderContext);
   }
}
