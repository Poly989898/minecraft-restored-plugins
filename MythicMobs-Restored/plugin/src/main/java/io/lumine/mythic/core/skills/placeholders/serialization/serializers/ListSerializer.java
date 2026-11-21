package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "list"
)
public class ListSerializer extends PlaceholderClassSerializer<List<String>> {
   @NotNull
   public String serialize(@NotNull List<String> object) {
      return String.join(",", object);
   }

   @NotNull
   public String format(@NotNull List<String> object) {
      return String.join(", ", object);
   }

   @NotNull
   public List<String> deserialize(@NotNull String object) {
      return Arrays.asList(object.split(","));
   }
}
