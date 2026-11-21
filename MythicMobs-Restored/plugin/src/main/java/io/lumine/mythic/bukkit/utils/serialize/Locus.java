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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public final class Locus implements GsonSerializable {
   private final double x;
   private final double y;
   private final double z;

   public static Locus deserialize(String element) {
      Preconditions.checkNotNull(element);
      String[] values = element.split(",");
      double x = Double.parseDouble(values[0]);
      double y = Double.parseDouble(values[1]);
      double z = Double.parseDouble(values[2]);
      return of(x, y, z);
   }

   public static Locus deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("x"));
      Preconditions.checkArgument(object.has("y"));
      Preconditions.checkArgument(object.has("z"));
      double x = object.get("x").getAsDouble();
      double y = object.get("y").getAsDouble();
      double z = object.get("z").getAsDouble();
      return of(x, y, z);
   }

   public static Locus of(double x, double y, double z) {
      return new Locus(x, y, z);
   }

   public static Locus of(Location location) {
      Objects.requireNonNull(location, "location");
      return of(location.getX(), location.getY(), location.getZ());
   }

   public static Locus of(Vector vector) {
      Objects.requireNonNull(vector, "vector");
      return of(vector.getX(), vector.getY(), vector.getZ());
   }

   public static Locus of(Block block) {
      Objects.requireNonNull(block, "block");
      return of(block.getLocation());
   }

   private Locus(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public Vector toVector() {
      return new Vector(this.x, this.y, this.z);
   }

   public synchronized Location toLocation(String world) {
      return this.toLocation(Bukkit.getWorld(world));
   }

   public synchronized Location toLocation(World world) {
      return new Location(world, this.x, this.y, this.z);
   }

   public synchronized Location toLocationCenter(World world) {
      return this.toPositionCenter(world).toLocation();
   }

   public synchronized Location toLocationCenterFloor(World world) {
      return this.toPositionCenterFloor(world).toLocation();
   }

   public synchronized Position toPosition(String world) {
      return this.toPosition(Bukkit.getWorld(world));
   }

   public synchronized Position toPosition(World world) {
      return Position.of(this.x, this.y, this.z, world);
   }

   public synchronized Position toPositionCenter(World world) {
      double cX = this.x + 0.5;
      double cZ = this.z + 0.5;
      return Position.of(cX, this.y + 0.5, cZ, world);
   }

   public synchronized Position toPositionCenterFloor(World world) {
      double cX = this.x + 0.5;
      double cZ = this.z + 0.5;
      return Position.of(cX, this.y, cZ, world);
   }

   public ChunkLocus toChunk() {
      return ChunkLocus.of((int)this.x >> 4, (int)this.z >> 4);
   }

   public BlockLocus floor() {
      return BlockLocus.of(bukkitFloor(this.x), bukkitFloor(this.y), bukkitFloor(this.z));
   }

   public Locus getRelative(BlockFace face) {
      Objects.requireNonNull(face, "face");
      return of(this.x + face.getModX(), this.y + face.getModY(), this.z + face.getModZ());
   }

   public Locus getRelative(BlockFace face, double distance) {
      Objects.requireNonNull(face, "face");
      return of(this.x + face.getModX() * distance, this.y + face.getModY() * distance, this.z + face.getModZ() * distance);
   }

   public Locus add(Vector vector3i) {
      return this.add(vector3i.getX(), vector3i.getY(), vector3i.getZ());
   }

   public Locus add(double x, double y, double z) {
      return of(this.x + x, this.y + y, this.z + z);
   }

   public Locus subtract(Vector vector3i) {
      return this.subtract(vector3i.getX(), vector3i.getY(), vector3i.getZ());
   }

   public Locus subtract(double x, double y, double z) {
      return this.add(-x, -y, -z);
   }

   public Zone zoneWith(Locus other) {
      Objects.requireNonNull(other, "other");
      return Zone.of(this, other);
   }

   public Orient withDirection(Direction direction) {
      return Orient.of(this, direction);
   }

   public Locus transform2D(double angle, double aboutX, double aboutZ, double translateX, double translateZ) {
      angle = Math.toRadians(angle);
      double x = this.x - aboutX;
      double z = this.z - aboutZ;
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double x2 = x * cos - z * sin;
      double z2 = x * sin + z * cos;
      return of(x2 + aboutX + translateX, this.y, z2 + aboutZ + translateZ);
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().add("x", this.x).add("y", this.y).add("z", this.z).build();
   }

   public String serializeShortForm() {
      return this.x + "," + this.y + "," + this.z;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof Locus other)
            ? false
            : Double.compare(this.getX(), other.getX()) == 0
               && Double.compare(this.getY(), other.getY()) == 0
               && Double.compare(this.getZ(), other.getZ()) == 0;
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long x = Double.doubleToLongBits(this.getX());
      long y = Double.doubleToLongBits(this.getY());
      long z = Double.doubleToLongBits(this.getZ());
      result = result * 59 + (int)(x >>> 32 ^ x);
      result = result * 59 + (int)(y >>> 32 ^ y);
      return result * 59 + (int)(z >>> 32 ^ z);
   }

   @Override
   public String toString() {
      return "Locus(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ")";
   }

   private static int bukkitFloor(double num) {
      int floor = (int)num;
      return floor == num ? floor : floor - (int)(Double.doubleToRawLongBits(num) >>> 63);
   }
}
