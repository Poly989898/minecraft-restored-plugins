package io.lumine.mythic.core.skills.placeholders.parsers.tokens;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import lombok.Getter;

@Getter
public abstract class PlaceholderParserToken<T> {
   protected final T value;

   PlaceholderParserToken(T value) {
      this.value = value;
   }

   public abstract boolean isStatic();

   public abstract String apply(PlaceholderContext placeholderContext);
}
