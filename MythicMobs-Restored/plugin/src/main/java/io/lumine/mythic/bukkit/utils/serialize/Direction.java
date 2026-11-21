package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.Location;

public final class Direction implements GsonSerializable {
   public static final Direction ZERO = of(0.0F, 0.0F);
   private final float yaw;
   private final float pitch;

   public static Direction deserialize(String element) {
      Preconditions.checkNotNull(element);
      String[] values = element.split(",");
      float yaw = Float.parseFloat(values[0]);
      float pitch = Float.parseFloat(values[1]);
      return of(yaw, pitch);
   }

   public static Direction deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("yaw"));
      Preconditions.checkArgument(object.has("pitch"));
      float yaw = object.get("yaw").getAsFloat();
      float pitch = object.get("pitch").getAsFloat();
      return of(yaw, pitch);
   }

   public static Direction of(float yaw, float pitch) {
      return new Direction(yaw, pitch);
   }

   public static Direction from(Location location) {
      Objects.requireNonNull(location, "location");
      return of(location.getYaw(), location.getPitch());
   }

   private Direction(float yaw, float pitch) {
      this.yaw = yaw;
      this.pitch = pitch;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public Direction round() {
      return this.round(2);
   }

   public Direction round(int places) {
      float yaw = (float)Numbers.round(this.yaw, places);
      float pitch = (float)Numbers.round(this.pitch, places);
      return of(yaw, pitch);
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().add("yaw", this.yaw).add("pitch", this.pitch).build();
   }

   public String serializeShortForm() {
      return this.yaw + "," + this.pitch;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof Direction other)
            ? false
            : Float.compare(this.getYaw(), other.getYaw()) == 0 && Float.compare(this.getPitch(), other.getPitch()) == 0;
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + Float.floatToIntBits(this.getYaw());
      return result * 59 + Float.floatToIntBits(this.getPitch());
   }

   @Override
   public String toString() {
      return "Direction(yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ")";
   }
}
