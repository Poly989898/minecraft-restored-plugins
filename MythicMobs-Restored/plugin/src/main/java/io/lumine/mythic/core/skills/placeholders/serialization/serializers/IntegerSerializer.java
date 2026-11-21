package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "integer"
)
public class IntegerSerializer extends PlaceholderClassSerializer<Integer> {
   @NotNull
   public String serialize(@NotNull Integer object) {
      return Integer.toString(object);
   }

   @NotNull
   public Integer deserialize(@NotNull String object) {
      return Integer.valueOf(object);
   }
}
