package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import io.lumine.mythic.core.skills.variables.types.VectorVariable;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "vector"
)
public class VectorSerializer extends PlaceholderClassSerializer<AbstractVector> {
   @NotNull
   public String serialize(@NotNull AbstractVector object) {
      return VectorVariable.toString(object);
   }

   @NotNull
   public String format(@NotNull AbstractVector object) {
      return VectorVariable.formatted(object);
   }

   @NotNull
   public AbstractVector deserialize(@NotNull String object) {
      return VectorVariable.fromString(object);
   }

   public static AbstractVector toVector(String rawData) {
      if (rawData != null && !rawData.trim().isEmpty()) {
         String[] split = rawData.trim().split(",");
         if (split.length == 3) {
            try {
               double x = Double.parseDouble(split[0].trim());
               double y = Double.parseDouble(split[1].trim());
               double z = Double.parseDouble(split[2].trim());
               return new AbstractVector(x, y, z);
            } catch (NumberFormatException e) {
               return new AbstractVector(0, 0, 0);
            }
         } else {
            return new AbstractVector(0, 0, 0);
         }
      } else {
         return new AbstractVector(0, 0, 0);
      }
   }
}
