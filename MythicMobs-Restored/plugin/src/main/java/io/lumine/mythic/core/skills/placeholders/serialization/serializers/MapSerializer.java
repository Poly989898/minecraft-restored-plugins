package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import io.lumine.mythic.core.skills.variables.types.MapVariable;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "map"
)
public class MapSerializer extends PlaceholderClassSerializer<Map<String, String>> {
   @NotNull
   public String serialize(@NotNull Map<String, String> object) {
      if (object.isEmpty()) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();
         boolean first = true;

         for (Entry<String, String> entry : object.entrySet()) {
            if (!first) {
               sb.append(';');
            }

            sb.append(entry.getKey()).append('=').append(entry.getValue());
            first = false;
         }

         return sb.toString();
      }
   }

   @NotNull
   public Map<String, String> deserialize(@NotNull String object) {
      return MapVariable.parse(object);
   }
}
