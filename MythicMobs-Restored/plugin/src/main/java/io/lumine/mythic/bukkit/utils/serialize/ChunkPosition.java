package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.cache.Lazy;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

public final class ChunkPosition implements GsonSerializable {
   private final int x;
   private final int z;
   private final String world;
   private final Lazy<Collection<BlockPosition>> blocks = Lazy.suppliedBy(() -> {
      List<BlockPosition> blocks = new ArrayList<>(65536);

      for (int xx = 0; xx < 16; xx++) {
         for (int zx = 0; zx < 16; zx++) {
            for (int y = 0; y < 256; y++) {
               blocks.add(this.getBlock(xx, y, zx));
            }
         }
      }

      return Collections.unmodifiableList(blocks);
   });

   public static ChunkPosition deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("x"));
      Preconditions.checkArgument(object.has("z"));
      Preconditions.checkArgument(object.has("world"));
      int x = object.get("x").getAsInt();
      int z = object.get("z").getAsInt();
      String world = object.get("world").getAsString();
      return of(x, z, world);
   }

   public static ChunkPosition of(int x, int z, String world) {
      Objects.requireNonNull(world, "world");
      return new ChunkPosition(x, z, world);
   }

   public static ChunkPosition of(int x, int z, World world) {
      Objects.requireNonNull(world, "world");
      return of(x, z, world.getName());
   }

   public static ChunkPosition of(Chunk location) {
      Objects.requireNonNull(location, "location");
      return of(location.getX(), location.getZ(), location.getWorld().getName());
   }

   private ChunkPosition(int x, int z, String world) {
      this.x = x;
      this.z = z;
      this.world = world;
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public String getWorld() {
      return this.world;
   }

   public Chunk toChunk() {
      return Bukkit.getWorld(this.world).getChunkAt(this.x, this.z);
   }

   public boolean contains(BlockPosition block) {
      return this.equals(block.toChunk());
   }

   public boolean contains(Position position) {
      return this.equals(position.floor().toChunk());
   }

   public BlockPosition getBlock(int x, int y, int z) {
      return BlockPosition.of(this.x << 4 | x & 15, y, this.z << 4 | z & 15, this.world);
   }

   public Collection<BlockPosition> blocks() {
      return this.blocks.get();
   }

   public ChunkPosition getRelative(BlockFace face) {
      Preconditions.checkArgument(face != BlockFace.UP && face != BlockFace.DOWN, "invalid face");
      return of(this.x + face.getModX(), this.z + face.getModZ(), this.world);
   }

   public ChunkPosition getRelative(BlockFace face, int distance) {
      Preconditions.checkArgument(face != BlockFace.UP && face != BlockFace.DOWN, "invalid face");
      return of(this.x + face.getModX() * distance, this.z + face.getModZ() * distance, this.world);
   }

   public ChunkPosition add(int x, int z) {
      return of(this.x + x, this.z + z, this.world);
   }

   public ChunkPosition subtract(int x, int z) {
      return this.add(-x, -z);
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().add("x", this.x).add("z", this.z).add("world", this.world).build();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof ChunkPosition other)
            ? false
            : this.getX() == other.getX() && this.getZ() == other.getZ() && this.getWorld().equals(other.getWorld());
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getX();
      result = result * 59 + this.getZ();
      return result * 59 + this.getWorld().hashCode();
   }

   @Override
   public String toString() {
      return "ChunkPosition(x=" + this.getX() + ", z=" + this.getZ() + ", world=" + this.getWorld() + ")";
   }
}
