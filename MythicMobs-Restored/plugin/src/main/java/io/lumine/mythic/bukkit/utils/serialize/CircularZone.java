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
import org.bukkit.World;
import org.bukkit.block.Block;

public class CircularZone implements GsonSerializable {
   private final Locus center;
   private final double radius;
   private final double diameter;

   public static CircularZone deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("center"));
      Preconditions.checkArgument(object.has("radius"));
      Locus center = Locus.deserialize(object.get("center"));
      double radius = object.get("radius").getAsDouble();
      return of(center, radius);
   }

   public static CircularZone of(Locus center, double radius) {
      Objects.requireNonNull(center, "center");
      if (radius <= 0.0) {
         throw new IllegalArgumentException("radius cannot be negative");
      } else {
         return new CircularZone(center, radius);
      }
   }

   private CircularZone(Locus center, double radius) {
      this.center = center;
      this.radius = radius;
      this.diameter = radius * 2.0;
   }

   public boolean inRegion(Locus pos) {
      Objects.requireNonNull(pos, "pos");
      return pos.toVector().distanceSquared(this.center.toVector()) < this.radius * this.radius;
   }

   public boolean inRegion(Block block) {
      Objects.requireNonNull(block, "block");
      return block.getLocation().distanceSquared(this.center.toLocation(block.getWorld())) < this.radius * this.radius;
   }

   public Locus getCenter() {
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

   public CircularRegion toRegion(String world) {
      return CircularRegion.of(this.center.toPosition(world), this.radius);
   }

   public CircularRegion toRegion(World world) {
      return CircularRegion.of(this.center.toPosition(world), this.radius);
   }

   @Nonnull
   public Set<BlockLocus> getOuterBlockPositions() {
      Set<BlockLocus> positions = new HashSet<>((int)this.getCircumference());

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
         CircularZone that = (CircularZone)o;
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
      return "CircularZone{center=" + this.center + ", radius=" + this.radius + "}";
   }
}
