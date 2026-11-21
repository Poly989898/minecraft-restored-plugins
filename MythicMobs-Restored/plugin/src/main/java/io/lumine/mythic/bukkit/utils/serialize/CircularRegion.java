package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import io.lumine.mythic.bukkit.utils.numbers.Maths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import org.bukkit.block.Block;

public class CircularRegion implements GsonSerializable {
   private final Position center;
   private final double radius;
   private final double diameter;

   public static CircularRegion deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("center"));
      Preconditions.checkArgument(object.has("radius"));
      Position center = Position.deserialize(object.get("center"));
      double radius = object.get("radius").getAsDouble();
      return of(center, radius);
   }

   public static CircularRegion of(Position center, double radius) {
      Objects.requireNonNull(center, "center");
      if (radius <= 0.0) {
         throw new IllegalArgumentException("radius cannot be negative");
      } else {
         return new CircularRegion(center, radius);
      }
   }

   private CircularRegion(Position center, double radius) {
      this.center = center;
      this.radius = radius;
      this.diameter = radius * 2.0;
   }

   public boolean inRegion(Position pos) {
      Objects.requireNonNull(pos, "pos");
      return pos.toVector().distanceSquared(this.center.toVector()) < this.radius * this.radius;
   }

   public boolean inRegion(Block block) {
      Objects.requireNonNull(block, "block");
      return block.getLocation().distanceSquared(this.center.toLocation()) < this.radius * this.radius;
   }

   public Position getCenter() {
      return this.center;
   }

   public double getRadius() {
      return this.radius;
   }

   public double getDiameter() {
      return this.diameter;
   }

   public double getCircumference() {
      return (Math.PI * 2) * this.radius;
   }

   @Nonnull
   public Set<BlockPosition> getOuterBlockPositions() {
      Set<BlockPosition> positions = new HashSet<>((int)this.getCircumference());

      for (int degree = 0; degree < 360; degree++) {
         float radian = Maths.toRadians(degree);
         double x = Maths.cos(radian) * this.radius;
         double z = Maths.sin(radian) * this.radius;
         positions.add(this.center.add((int)x, 0.0, (int)z).floor());
      }

      return Collections.unmodifiableSet(positions);
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().add("center", this.center).add("radius", this.radius).build();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CircularRegion that = (CircularRegion)o;
         return Double.compare(that.radius, this.radius) == 0 && this.center.equals(that.center);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.center, this.radius);
   }

   @Override
   public String toString() {
      return "CircularRegion{center=" + this.center + ", radius=" + this.radius + "}";
   }
}
