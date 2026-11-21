package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public final class Zone implements GsonSerializable {
   private final Locus min;
   private final Locus max;
   private final double width;
   private final double height;
   private final double depth;

   public static Zone deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("min"));
      Preconditions.checkArgument(object.has("max"));
      Locus a = Locus.deserialize(object.get("min"));
      Locus b = Locus.deserialize(object.get("max"));
      return of(a, b);
   }

   public static Zone of(Locus a, Locus b) {
      Objects.requireNonNull(a, "a");
      Objects.requireNonNull(b, "b");
      return new Zone(a, b);
   }

   public static Zone of(BoundingBox box) {
      return new Zone(Locus.of(box.getMin()), Locus.of(box.getMax()));
   }

   private Zone(Locus a, Locus b) {
      this.min = Locus.of(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
      this.max = Locus.of(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
      this.width = this.max.getX() - this.min.getX();
      this.height = this.max.getY() - this.min.getY();
      this.depth = this.max.getZ() - this.min.getZ();
   }

   public boolean inRegion(Position pos) {
      Objects.requireNonNull(pos, "pos");
      return this.inRegion(pos.getX(), pos.getY(), pos.getZ());
   }

   public boolean inRegion(Location loc) {
      Objects.requireNonNull(loc, "loc");
      return this.inRegion(loc.getX(), loc.getY(), loc.getZ());
   }

   public boolean inRegion(double x, double y, double z) {
      return x >= this.min.getX() && x <= this.max.getX() && y >= this.min.getY() && y <= this.max.getY() && z >= this.min.getZ() && z <= this.max.getZ();
   }

   public boolean inBlockRegion(Position pos) {
      Objects.requireNonNull(pos, "pos");
      Location loc = pos.toLocation();
      return this.inRegion(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
   }

   public boolean inBlockRegion(Location loc) {
      Objects.requireNonNull(loc, "loc");
      return this.inRegion(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
   }

   public Region toRegion(String world) {
      return this.toRegion(Bukkit.getWorld(world));
   }

   public Region toRegion(World world) {
      return Region.of(this.min.toPosition(world), this.max.toPosition(world));
   }

   public Locus getMin() {
      return this.min;
   }

   public Locus getMax() {
      return this.max;
   }

   public double getWidth() {
      return this.width;
   }

   public double getHeight() {
      return this.height;
   }

   public double getDepth() {
      return this.depth;
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().add("min", this.min).add("max", this.max).build();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof Zone other) ? false : this.getMin().equals(other.getMin()) && this.getMax().equals(other.getMax());
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getMin().hashCode();
      return result * 59 + this.getMax().hashCode();
   }

   @Override
   public String toString() {
      return "Zone(min=" + this.getMin() + ", max=" + this.getMax() + ")";
   }
}
