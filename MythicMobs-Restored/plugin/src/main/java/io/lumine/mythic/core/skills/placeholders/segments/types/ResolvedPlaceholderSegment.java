package io.lumine.mythic.core.skills.placeholders.segments.types;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import java.util.function.BiFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ResolvedPlaceholderSegment<PARSER>(@Nullable PARSER value, @Nullable MythicLineConfig config) {
   @Contract(
      pure = true,
      value = "_, _, !null -> !null"
   )
   public <VALUE> VALUE getOrDefault(
      @NotNull PlaceholderContext context, @NotNull BiFunction<PARSER, PlaceholderContext, VALUE> getter, @Nullable VALUE defaultValue
   ) {
      VALUE value = this.get(context, getter);
      return value == null ? defaultValue : value;
   }

   public <VALUE> VALUE get(@NotNull PlaceholderContext context, @NotNull BiFunction<PARSER, PlaceholderContext, VALUE> getter) {
      return this.value == null ? null : getter.apply(this.value, context);
   }
}
