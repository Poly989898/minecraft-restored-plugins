package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.Location;
import org.bukkit.World;

public final class Orient implements GsonSerializable {
   private final Locus locus;
   private final Direction direction;

   public static Orient deserialize(String element) {
      return deserialize(element, of(0.0, 0.0, 0.0, 0.0F, 0.0F));
   }

   public static Orient deserialize(String element, Orient def) {
      Preconditions.checkNotNull(element);
      String[] values = element.split(",");
      double x = Double.parseDouble(values[0]);
      double y = Double.parseDouble(values[1]);
      double z = Double.parseDouble(values[2]);
      float yaw = values.length > 3 ? Float.parseFloat(values[3]) : def.getDirection().getYaw();
      float pitch = values.length > 4 ? Float.parseFloat(values[4]) : def.getDirection().getPitch();
      return of(x, y, z, yaw, pitch);
   }

   public static Orient deserialize(JsonElement element) {
      Locus locus = Locus.deserialize(element);
      Direction direction = Direction.deserialize(element);
      return of(locus, direction);
   }

   public static Orient of(Locus locus, Direction direction) {
      Objects.requireNonNull(locus, "locus");
      Objects.requireNonNull(direction, "direction");
      return new Orient(locus, direction);
   }

   public static Orient of(Location location) {
      Objects.requireNonNull(location, "location");
      return of(Locus.of(location), Direction.from(location));
   }

   public static Orient of(double x, double y, double z, float yaw, float pitch) {
      return of(Locus.of(x, y, z), Direction.of(yaw, pitch));
   }

   private Orient(Locus locus, Direction direction) {
      this.locus = locus;
      this.direction = direction;
   }

   public Locus getLocus() {
      return this.locus;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public synchronized Location toLocation(World world) {
      return new Location(world, this.locus.getX(), this.locus.getY(), this.locus.getZ(), this.direction.getYaw(), this.direction.getPitch());
   }

   public Orient add(double x, double y, double z) {
      return this.locus.add(x, y, z).withDirection(this.direction);
   }

   public Orient subtract(double x, double y, double z) {
      return this.locus.subtract(x, y, z).withDirection(this.direction);
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().addAll(this.locus.serialize()).addAll(this.direction.serialize()).build();
   }

   public String serializeShortForm() {
      return this.locus.serializeShortForm() + "," + this.direction.serializeShortForm();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof Orient other) ? false : this.getLocus().equals(other.getLocus()) && this.getDirection().equals(other.getDirection());
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getLocus().hashCode();
      return result * 59 + this.getDirection().hashCode();
   }

   @Override
   public String toString() {
      return "Orient(locus=" + this.getLocus() + ", direction=" + this.getDirection() + ")";
   }
}
