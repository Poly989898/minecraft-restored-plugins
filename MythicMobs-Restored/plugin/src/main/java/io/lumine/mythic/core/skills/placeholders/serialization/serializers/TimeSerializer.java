package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import io.lumine.mythic.core.skills.variables.types.TimeVariable;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "time"
)
public class TimeSerializer extends PlaceholderClassSerializer<Instant> {
   @NotNull
   public String serialize(@NotNull Instant object) {
      return TimeVariable.toString(object);
   }

   @NotNull
   public Instant deserialize(@NotNull String object) {
      return TimeVariable.fromString(object);
   }
}
