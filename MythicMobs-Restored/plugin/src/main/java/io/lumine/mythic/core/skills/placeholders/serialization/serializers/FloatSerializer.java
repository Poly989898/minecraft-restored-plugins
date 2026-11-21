package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "float"
)
public class FloatSerializer extends PlaceholderClassSerializer<Float> {
   @NotNull
   public String serialize(@NotNull Float object) {
      return Float.toString(object);
   }

   @NotNull
   public Float deserialize(@NotNull String object) {
      return Float.valueOf(object);
   }
}
