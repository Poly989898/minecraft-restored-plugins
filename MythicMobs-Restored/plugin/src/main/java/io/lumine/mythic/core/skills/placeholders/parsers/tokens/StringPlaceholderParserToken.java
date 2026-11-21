package io.lumine.mythic.core.skills.placeholders.parsers.tokens;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;

public class StringPlaceholderParserToken extends PlaceholderParserToken<String> {
   public StringPlaceholderParserToken(String value) {
      super(value);
   }

   @Override
   public boolean isStatic() {
      return true;
   }

   @Override
   public String apply(PlaceholderContext placeholderContext) {
      return this.value;
   }
}
