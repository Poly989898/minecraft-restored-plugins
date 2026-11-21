package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "string"
)
public class StringSerializer extends PlaceholderClassSerializer<String> {
   @NotNull
   public String serialize(@NotNull String object) {
      return object;
   }

   @NotNull
   public String deserialize(@NotNull String object) {
      return object;
   }
}
