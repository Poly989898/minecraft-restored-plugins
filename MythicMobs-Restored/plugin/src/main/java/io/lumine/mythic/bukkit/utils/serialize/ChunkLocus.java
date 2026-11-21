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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

public final class ChunkLocus implements GsonSerializable {
   private final int x;
   private final int z;
   private final Lazy<Collection<BlockLocus>> blocks = Lazy.suppliedBy(() -> {
      List<BlockLocus> blocks = new ArrayList<>(65536);

      for (int xx = 0; xx < 16; xx++) {
         for (int zx = 0; zx < 16; zx++) {
            for (int y = 0; y < 256; y++) {
               blocks.add(this.getBlock(xx, y, zx));
            }
         }
      }

      return Collections.unmodifiableList(blocks);
   });

   public static ChunkLocus deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("x"));
      Preconditions.checkArgument(object.has("z"));
      int x = object.get("x").getAsInt();
      int z = object.get("z").getAsInt();
      return of(x, z);
   }

   public static ChunkLocus of(int x, int z) {
      return new ChunkLocus(x, z);
   }

   public static ChunkLocus of(Chunk location) {
      Objects.requireNonNull(location, "location");
      return of(location.getX(), location.getZ());
   }

   public static Collection<ChunkLocus> of(Region region) {
      return of(region.toZone());
   }

   public static Collection<ChunkLocus> of(Zone zone) {
      Collection<ChunkLocus> ret = new HashSet<>();

      for (int x = (int)zone.getMin().getX(); x <= zone.getMax().getX(); x++) {
         for (int z = (int)zone.getMin().getZ(); z <= zone.getMax().getZ(); z++) {
            ChunkLocus locus = of(x >> 4, z >> 4);
            if (!ret.contains(locus)) {
               ret.add(locus);
            }
         }
      }

      return ret;
   }

   private ChunkLocus(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public Chunk toChunk(String world) {
      return Bukkit.getWorld(world).getChunkAt(this.x, this.z);
   }

   public Chunk toChunk(World world) {
      return world.getChunkAt(this.x, this.z);
   }

   public boolean contains(BlockPosition block) {
      return this.equals(block.toChunk());
   }

   public boolean contains(Position position) {
      return this.equals(position.floor().toChunk());
   }

   public boolean contains(Locus position) {
      return this.equals(position.floor().toChunkLocus());
   }

   public BlockLocus getBlock(int x, int y, int z) {
      return BlockLocus.of(this.x << 4 | x & 15, y, this.z << 4 | z & 15);
   }

   public Locus getLocus(int x, int y, int z) {
      return Locus.of(this.x << 4 | x & 15, y, this.z << 4 | z & 15);
   }

   public Collection<BlockLocus> blocks() {
      return this.blocks.get();
   }

   public ChunkLocus getRelative(BlockFace face) {
      Preconditions.checkArgument(face != BlockFace.UP && face != BlockFace.DOWN, "invalid face");
      return of(this.x + face.getModX(), this.z + face.getModZ());
   }

   public ChunkLocus getRelative(BlockFace face, int distance) {
      Preconditions.checkArgument(face != BlockFace.UP && face != BlockFace.DOWN, "invalid face");
      return of(this.x + face.getModX() * distance, this.z + face.getModZ() * distance);
   }

   public ChunkLocus add(int x, int z) {
      return of(this.x + x, this.z + z);
   }

   public ChunkLocus subtract(int x, int z) {
      return this.add(-x, -z);
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().add("x", this.x).add("z", this.z).build();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o instanceof ChunkPosition other) {
         return this.getX() == other.getX() && this.getZ() == other.getZ();
      } else {
         return !(o instanceof ChunkLocus other) ? false : this.getX() == other.getX() && this.getZ() == other.getZ();
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getX();
      return result * 59 + this.getZ();
   }

   @Override
   public String toString() {
      return "ChunkLocus(x=" + this.getX() + ", z=" + this.getZ() + ")";
   }
}
