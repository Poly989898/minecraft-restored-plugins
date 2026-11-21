package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class Point implements GsonSerializable {
   private final Position position;
   private final Direction direction;
   @Nullable
   private Location bukkitLocation = null;

   public static Point deserialize(String element) {
      return deserialize(element, Orient.of(0.0, 0.0, 0.0, 0.0F, 0.0F));
   }

   public static Point deserialize(String element, Orient def) {
      Preconditions.checkNotNull(element);
      String[] values = element.split(",");
      String world = values[0];
      double x = Double.parseDouble(values[1]);
      double y = Double.parseDouble(values[2]);
      double z = Double.parseDouble(values[3]);
      float yaw = values.length > 3 ? Float.parseFloat(values[4]) : def.getDirection().getYaw();
      float pitch = values.length > 4 ? Float.parseFloat(values[5]) : def.getDirection().getPitch();
      return of(Position.of(x, y, z, world), Direction.of(yaw, pitch));
   }

   public static Point deserialize(JsonElement element) {
      Position position = Position.deserialize(element);
      Direction direction = Direction.deserialize(element);
      return of(position, direction);
   }

   public static Point of(Position position, Direction direction) {
      Objects.requireNonNull(position, "position");
      Objects.requireNonNull(direction, "direction");
      return new Point(position, direction);
   }

   public static Point of(Location location) {
      Objects.requireNonNull(location, "location");
      return of(Position.of(location), Direction.from(location));
   }

   private Point(Position position, Direction direction) {
      this.position = position;
      this.direction = direction;
   }

   public Position getPosition() {
      return this.position;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public synchronized Location toLocation() {
      if (this.bukkitLocation == null) {
         this.bukkitLocation = new Location(
            Bukkit.getWorld(this.position.getWorld()),
            this.position.getX(),
            this.position.getY(),
            this.position.getZ(),
            this.direction.getYaw(),
            this.direction.getPitch()
         );
      }

      return this.bukkitLocation.clone();
   }

   public Point add(double x, double y, double z) {
      return this.position.add(x, y, z).withDirection(this.direction);
   }

   public Point subtract(double x, double y, double z) {
      return this.position.subtract(x, y, z).withDirection(this.direction);
   }

   public Point round() {
      Position position = this.position.round();
      Direction direction = this.direction.round();
      return of(position, direction);
   }

   public Point round(int places) {
      Position position = this.position.round(places);
      Direction direction = this.direction.round();
      return of(position, direction);
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().addAll(this.position.serialize()).addAll(this.direction.serialize()).build();
   }

   public String serializeShortForm() {
      return this.position.serializeShortForm() + "," + this.direction.serializeShortForm();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof Point other) ? false : this.getPosition().equals(other.getPosition()) && this.getDirection().equals(other.getDirection());
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getPosition().hashCode();
      return result * 59 + this.getDirection().hashCode();
   }

   @Override
   public String toString() {
      return "Point(position=" + this.getPosition() + ", direction=" + this.getDirection() + ")";
   }
}
