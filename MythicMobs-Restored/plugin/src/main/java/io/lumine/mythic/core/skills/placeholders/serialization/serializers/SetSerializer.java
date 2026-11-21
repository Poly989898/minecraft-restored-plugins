package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import io.lumine.mythic.core.skills.variables.types.ListVariable;
import io.lumine.mythic.core.skills.variables.types.SetVariable;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "set"
)
public class SetSerializer extends PlaceholderClassSerializer<Set<String>> {
   @NotNull
   public String serialize(@NotNull Set<String> object) {
      return ListVariable.toString(object);
   }

   @NotNull
   public String format(@NotNull Set<String> object) {
      return ListVariable.formatted(object);
   }

   @NotNull
   public Set<String> deserialize(@NotNull String object) {
      return SetVariable.fromString(object);
   }
}
