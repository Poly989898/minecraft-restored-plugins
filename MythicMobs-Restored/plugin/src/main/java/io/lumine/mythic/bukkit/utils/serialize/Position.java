package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public final class Position implements GsonSerializable {
   private final double x;
   private final double y;
   private final double z;
   private final String world;
   @Nullable
   private Location bukkitLocation = null;

   public static Position deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("x"));
      Preconditions.checkArgument(object.has("y"));
      Preconditions.checkArgument(object.has("z"));
      Preconditions.checkArgument(object.has("world"));
      double x = object.get("x").getAsDouble();
      double y = object.get("y").getAsDouble();
      double z = object.get("z").getAsDouble();
      String world = object.get("world").getAsString();
      return of(x, y, z, world);
   }

   public static Position of(double x, double y, double z, String world) {
      Objects.requireNonNull(world, "world");
      return new Position(x, y, z, world);
   }

   public static Position of(double x, double y, double z, World world) {
      Objects.requireNonNull(world, "world");
      return of(x, y, z, world.getName());
   }

   public static Position of(Location location) {
      Objects.requireNonNull(location, "location");
      return of(location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
   }

   public static Position of(Block block) {
      Objects.requireNonNull(block, "block");
      return of(block.getLocation());
   }

   private Position(double x, double y, double z, String world) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.world = world;
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

   public double getBlockX() {
      return Numbers.floor(this.x);
   }

   public double getBlockY() {
      return Numbers.floor(this.y);
   }

   public double getBlockZ() {
      return Numbers.floor(this.z);
   }

   public int getChunkX() {
      return Numbers.floor(this.getBlockX() / 16.0);
   }

   public int getChunkZ() {
      return Numbers.floor(this.getBlockZ() / 16.0);
   }

   public long getChunkKey() {
      return this.getChunkX() & 4294967295L | (this.getChunkZ() & 4294967295L) << 32;
   }

   public String getWorld() {
      return this.world;
   }

   public Vector toVector() {
      return new Vector(this.x, this.y, this.z);
   }

   public synchronized Location toLocation() {
      if (this.bukkitLocation == null) {
         this.bukkitLocation = new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
      }

      return this.bukkitLocation.clone();
   }

   public synchronized Locus toLocus() {
      return Locus.of(this.x, this.y, this.z);
   }

   public synchronized BlockLocus toBlockLocus() {
      return BlockLocus.of((int)this.x, (int)this.y, (int)this.z);
   }

   public Position center() {
      return of(this.getBlockX() + 0.5, this.getBlockY() + 0.5, this.getBlockZ() + 0.5, this.world);
   }

   public BlockPosition floor() {
      return BlockPosition.of(bukkitFloor(this.x), bukkitFloor(this.y), bukkitFloor(this.z), this.world);
   }

   public Position getRelative(BlockFace face) {
      Objects.requireNonNull(face, "face");
      return of(this.x + face.getModX(), this.y + face.getModY(), this.z + face.getModZ(), this.world);
   }

   public Position getRelative(BlockFace face, double distance) {
      Objects.requireNonNull(face, "face");
      return of(this.x + face.getModX() * distance, this.y + face.getModY() * distance, this.z + face.getModZ() * distance, this.world);
   }

   public Position add(Vector vector3i) {
      return this.add(vector3i.getX(), vector3i.getY(), vector3i.getZ());
   }

   public Position add(double x, double y, double z) {
      return of(this.x + x, this.y + y, this.z + z, this.world);
   }

   public Position subtract(Vector vector3i) {
      return this.subtract(vector3i.getX(), vector3i.getY(), vector3i.getZ());
   }

   public Position subtract(double x, double y, double z) {
      return this.add(-x, -y, -z);
   }

   public Region regionWith(Position other) {
      Objects.requireNonNull(other, "other");
      return Region.of(this, other);
   }

   public Point withDirection(Direction direction) {
      return Point.of(this, direction);
   }

   public Position round() {
      return this.round(2);
   }

   public Position round(int places) {
      double x = Numbers.round(this.x, places);
      double y = Numbers.round(this.y, places);
      double z = Numbers.round(this.z, places);
      return of(x, y, z, this.world);
   }

   public Position transform2D(double angle, double aboutX, double aboutZ, double translateX, double translateZ) {
      angle = Math.toRadians(angle);
      double x = this.x - aboutX;
      double z = this.z - aboutZ;
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double x2 = x * cos - z * sin;
      double z2 = x * sin + z * cos;
      return of(x2 + aboutX + translateX, this.y, z2 + aboutZ + translateZ, this.world);
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().add("x", this.x).add("y", this.y).add("z", this.z).add("world", this.world).build();
   }

   public String serializeShortForm() {
      return this.world + "," + this.x + "," + this.y + "," + this.z;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof Position other)
            ? false
            : Double.compare(this.getX(), other.getX()) == 0
               && Double.compare(this.getY(), other.getY()) == 0
               && Double.compare(this.getZ(), other.getZ()) == 0
               && this.getWorld().equals(other.getWorld());
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
      result = result * 59 + (int)(z >>> 32 ^ z);
      return result * 59 + this.getWorld().hashCode();
   }

   @Override
   public String toString() {
      return "Position(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", world=" + this.getWorld() + ")";
   }

   private static int bukkitFloor(double num) {
      int floor = (int)num;
      return floor == num ? floor : floor - (int)(Double.doubleToRawLongBits(num) >>> 63);
   }
}
