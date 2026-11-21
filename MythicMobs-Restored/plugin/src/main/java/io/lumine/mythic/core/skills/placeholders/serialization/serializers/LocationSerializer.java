package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "location"
)
public class LocationSerializer extends PlaceholderClassSerializer<AbstractLocation> {
   @NotNull
   public String serialize(@NotNull AbstractLocation object) {
      return object.getWorld().getName() + "," + object.getX() + "," + object.getY() + "," + object.getZ();
   }

   @NotNull
   public String format(@NotNull AbstractLocation object) {
      return object.getWorld().getName() + ", " + object.getX() + ", " + object.getY() + ", " + object.getZ();
   }

   @NotNull
   public AbstractLocation deserialize(@NotNull String object) {
      return AbstractLocation.deserializeFromVariableLocation(object);
   }
}
