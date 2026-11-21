package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import java.util.function.BiFunction;

@Deprecated
public class WorldPlaceholder implements Placeholder, BiFunction<AbstractWorld, String, String> {
   private final BiFunction<AbstractWorld, String, String> transformer;

   public WorldPlaceholder(BiFunction<AbstractWorld, String, String> transformer) {
      this.transformer = transformer;
   }

   public String apply(AbstractWorld spawner, String conditional) {
      return this.transformer.apply(spawner, conditional);
   }
}
