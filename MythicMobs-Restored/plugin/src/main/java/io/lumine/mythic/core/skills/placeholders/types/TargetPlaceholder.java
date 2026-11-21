package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.utils.interfaces.TriFunction;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

@Deprecated
public class TargetPlaceholder implements Placeholder, TriFunction<PlaceholderMeta, AbstractEntity, String, String> {
   private final TriFunction<PlaceholderMeta, AbstractEntity, String, String> transformer;

   public TargetPlaceholder(TriFunction<PlaceholderMeta, AbstractEntity, String, String> transformer) {
      this.transformer = transformer;
   }

   public String apply(PlaceholderMeta meta, AbstractEntity target, String conditional) {
      return this.transformer.apply(meta, target, conditional);
   }
}
