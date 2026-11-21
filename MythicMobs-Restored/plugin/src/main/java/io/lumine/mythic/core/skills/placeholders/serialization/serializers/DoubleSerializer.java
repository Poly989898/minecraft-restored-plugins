package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "double"
)
public class DoubleSerializer extends PlaceholderClassSerializer<Double> {
   @NotNull
   public String serialize(@NotNull Double object) {
      return Double.toString(object);
   }

   @NotNull
   public Double deserialize(@NotNull String object) {
      return Double.valueOf(object);
   }
}
