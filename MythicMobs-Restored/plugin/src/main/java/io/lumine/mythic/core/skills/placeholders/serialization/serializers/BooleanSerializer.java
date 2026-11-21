package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "boolean"
)
public class BooleanSerializer extends PlaceholderClassSerializer<Boolean> {
   @NotNull
   public String serialize(@NotNull Boolean object) {
      return Boolean.toString(object);
   }

   @NotNull
   public Boolean deserialize(@NotNull String object) {
      return PolymorphicPlaceholder.of(object).getBoolean(PlaceholderContext.of());
   }
}
