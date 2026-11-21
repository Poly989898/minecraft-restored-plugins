package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "long"
)
public class LongSerializer extends PlaceholderClassSerializer<Long> {
   @NotNull
   public String serialize(@NotNull Long object) {
      return Long.toString(object);
   }

   @NotNull
   public Long deserialize(@NotNull String object) {
      return Long.valueOf(object);
   }
}
