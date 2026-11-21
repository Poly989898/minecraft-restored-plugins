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

public final class BlockPosition implements GsonSerializable {
   private final int x;
   private final int y;
   private final int z;
   private final String world;
   @Nullable
   private Location bukkitLocation = null;

   public static BlockPosition deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("x"));
      Preconditions.checkArgument(object.has("y"));
      Preconditions.checkArgument(object.has("z"));
      Preconditions.checkArgument(object.has("world"));
      int x = object.get("x").getAsInt();
      int y = object.get("y").getAsInt();
      int z = object.get("z").getAsInt();
      String world = object.get("world").getAsString();
      return of(x, y, z, world);
   }

   public static BlockPosition of(int x, int y, int z, String world) {
      Objects.requireNonNull(world, "world");
      return new BlockPosition(x, y, z, world);
   }

   public static BlockPosition of(int x, int y, int z, World world) {
      Objects.requireNonNull(world, "world");
      return of(x, y, z, world.getName());
   }

   public static BlockPosition of(Location location) {
      Objects.requireNonNull(location, "location");
      return of(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
   }

   public static BlockPosition of(Block block) {
      Objects.requireNonNull(block, "block");
      return of(block.getLocation());
   }

   private BlockPosition(int x, int y, int z, String world) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.world = world;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public String getWorld() {
      return this.world;
   }

   public synchronized Location toLocation() {
      if (this.bukkitLocation == null) {
         this.bukkitLocation = new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
      }

      return this.bukkitLocation.clone();
   }

   public Vector toVector() {
      return new Vector(this.x, this.y, this.z);
   }

   public Block toBlock() {
      return this.toLocation().getBlock();
   }

   public Position toPosition() {
      return Position.of((double)this.x, (double)this.y, (double)this.z, this.world);
   }

   public Position toPositionCenter() {
      return Position.of(this.x + 0.5, this.y + 0.5, this.z + 0.5, this.world);
   }

   public ChunkPosition toChunk() {
      return ChunkPosition.of(this.x >> 4, this.z >> 4, this.world);
   }

   public boolean contains(Position position) {
      return this.equals(position.floor());
   }

   public BlockPosition getRelative(BlockFace face) {
      Objects.requireNonNull(face, "face");
      return of(this.x + face.getModX(), this.y + face.getModY(), this.z + face.getModZ(), this.world);
   }

   public BlockPosition getRelative(BlockFace face, int distance) {
      Objects.requireNonNull(face, "face");
      return of(this.x + face.getModX() * distance, this.y + face.getModY() * distance, this.z + face.getModZ() * distance, this.world);
   }

   public BlockPosition add(Vector vector3i) {
      return this.add((int)vector3i.getX(), (int)vector3i.getY(), (int)vector3i.getZ());
   }

   public BlockPosition add(int x, int y, int z) {
      return of(this.x + x, this.y + y, this.z + z, this.world);
   }

   public BlockPosition subtract(int x, int y, int z) {
      return this.add(-x, -y, -z);
   }

   public BlockRegion regionWith(BlockPosition other) {
      Objects.requireNonNull(other, "other");
      return BlockRegion.of(this, other);
   }

   public BlockPosition transform2D(double angle, double aboutX, double aboutZ, double translateX, double translateZ) {
      angle = Math.toRadians(angle);
      double x = this.x - aboutX;
      double z = this.z - aboutZ;
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double x2 = x * cos - z * sin;
      double z2 = x * sin + z * cos;
      return of(Numbers.floor(x2 + aboutX + translateX), this.y, Numbers.floor(z2 + aboutZ + translateZ), this.world);
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().add("x", this.x).add("y", this.y).add("z", this.z).add("world", this.world).build();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof BlockPosition other)
            ? false
            : this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ() && this.getWorld().equals(other.getWorld());
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getX();
      result = result * 59 + this.getY();
      result = result * 59 + this.getZ();
      return result * 59 + this.getWorld().hashCode();
   }

   @Override
   public String toString() {
      return "BlockPosition(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", world=" + this.getWorld() + ")";
   }
}
