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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public final class BlockLocus implements GsonSerializable {
   private final int x;
   private final int y;
   private final int z;

   public static BlockLocus deserialize(String element) {
      Preconditions.checkNotNull(element);
      String[] values = element.split(",");
      int x = Integer.parseInt(values[0]);
      int y = Integer.parseInt(values[1]);
      int z = Integer.parseInt(values[2]);
      return of(x, y, z);
   }

   public static BlockLocus deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("x"));
      Preconditions.checkArgument(object.has("y"));
      Preconditions.checkArgument(object.has("z"));
      int x = object.get("x").getAsInt();
      int y = object.get("y").getAsInt();
      int z = object.get("z").getAsInt();
      return of(x, y, z);
   }

   public static BlockLocus of(int x, int y, int z) {
      return new BlockLocus(x, y, z);
   }

   public static BlockLocus of(Location location) {
      Objects.requireNonNull(location, "location");
      return of(location.getBlockX(), location.getBlockY(), location.getBlockZ());
   }

   public static BlockLocus of(Block block) {
      Objects.requireNonNull(block, "block");
      return of(block.getLocation());
   }

   private BlockLocus(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
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

   public synchronized Location toLocation(World world) {
      return new Location(world, this.x, this.y, this.z);
   }

   public synchronized Location toLocationCenter(World world) {
      double cX = this.x >= 0 ? this.x + 0.5 : this.x + -0.5;
      double cZ = this.z >= 0 ? this.z + 0.5 : this.z + -0.5;
      return new Location(world, cX, this.y + 0.5, cZ);
   }

   public Vector toVector() {
      return new Vector(this.x, this.y, this.z);
   }

   public Block toBlock(World world) {
      return this.toLocation(world).getBlock();
   }

   public synchronized Position toPosition(World world) {
      return Position.of((double)this.x, (double)this.y, (double)this.z, world);
   }

   public synchronized Position toPositionCenter(World world) {
      double cX = this.x >= 0 ? this.x + 0.5 : this.x + -0.5;
      double cZ = this.z >= 0 ? this.z + 0.5 : this.z + -0.5;
      return Position.of(cX, this.y + 0.5, cZ, world);
   }

   public ChunkPosition toChunk(World world) {
      return ChunkPosition.of(this.x >> 4, this.z >> 4, world);
   }

   public ChunkLocus toChunkLocus() {
      return ChunkLocus.of(this.x >> 4, this.z >> 4);
   }

   public boolean contains(Position position) {
      return this.equals(position.floor());
   }

   public BlockLocus getRelative(BlockFace face) {
      Objects.requireNonNull(face, "face");
      return of(this.x + face.getModX(), this.y + face.getModY(), this.z + face.getModZ());
   }

   public BlockLocus getRelative(BlockFace face, int distance) {
      Objects.requireNonNull(face, "face");
      return of(this.x + face.getModX() * distance, this.y + face.getModY() * distance, this.z + face.getModZ() * distance);
   }

   public BlockLocus add(Vector vector3i) {
      return this.add((int)vector3i.getX(), (int)vector3i.getY(), (int)vector3i.getZ());
   }

   public BlockLocus add(int x, int y, int z) {
      return of(this.x + x, this.y + y, this.z + z);
   }

   public BlockLocus subtract(int x, int y, int z) {
      return this.add(-x, -y, -z);
   }

   public BlockOrient withDirection(Direction direction) {
      return BlockOrient.of(this, direction);
   }

   public BlockLocus transform2D(double angle, int aboutX, int aboutZ, int translateX, int translateZ) {
      angle = Numbers.round(angle / 90.0) * 90.0;
      if (angle == 0.0) {
         return of(Numbers.floor(this.x + translateX), this.y, Numbers.floor(this.z + translateZ));
      } else {
         double aX = aboutX;
         double aZ = aboutZ;
         angle = Math.toRadians(angle);
         double x = this.x - aX;
         double z = this.z - aZ;
         double cos = Math.cos(angle);
         double sin = Math.sin(angle);
         double x2 = x * cos - z * sin;
         double z2 = x * sin + z * cos;
         return of(Numbers.floor(x2 + aX + translateX), this.y, Numbers.floor(z2 + aZ + translateZ));
      }
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
         return !(o instanceof BlockLocus other) ? false : this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ();
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getX();
      result = result * 59 + this.getY();
      return result * 59 + this.getZ();
   }

   @Override
   public String toString() {
      return "BlockLocus(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ")";
   }
}
