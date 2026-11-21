package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import java.util.function.BiFunction;

@Deprecated
public class LocationPlaceholder implements Placeholder, BiFunction<AbstractLocation, String, String> {
   private final BiFunction<AbstractLocation, String, String> transformer;

   public LocationPlaceholder(BiFunction<AbstractLocation, String, String> transformer) {
      this.transformer = transformer;
   }

   public String apply(AbstractLocation target, String conditional) {
      return this.transformer.apply(target, conditional);
   }
}
