package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.block.Block;

public final class BlockRegion implements GsonSerializable {
   private final BlockPosition min;
   private final BlockPosition max;
   private final int width;
   private final int height;
   private final int depth;

   public static BlockRegion deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("min"));
      Preconditions.checkArgument(object.has("max"));
      BlockPosition a = BlockPosition.deserialize(object.get("min"));
      BlockPosition b = BlockPosition.deserialize(object.get("max"));
      return of(a, b);
   }

   public static BlockRegion of(BlockPosition a, BlockPosition b) {
      Objects.requireNonNull(a, "a");
      Objects.requireNonNull(b, "b");
      if (!a.getWorld().equals(b.getWorld())) {
         throw new IllegalArgumentException("positions are in different worlds");
      } else {
         return new BlockRegion(a, b);
      }
   }

   private BlockRegion(BlockPosition a, BlockPosition b) {
      this.min = BlockPosition.of(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()), a.getWorld());
      this.max = BlockPosition.of(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()), a.getWorld());
      this.width = this.max.getX() - this.min.getX();
      this.height = this.max.getY() - this.min.getY();
      this.depth = this.max.getZ() - this.min.getZ();
   }

   public boolean inRegion(BlockPosition pos) {
      Objects.requireNonNull(pos, "pos");
      return pos.getWorld().equals(this.min.getWorld()) && this.inRegion(pos.getX(), pos.getY(), pos.getZ());
   }

   public boolean inRegion(Block block) {
      Objects.requireNonNull(block, "block");
      return block.getWorld().getName().equals(this.min.getWorld()) && this.inRegion(block.getX(), block.getY(), block.getZ());
   }

   public boolean inRegion(int x, int y, int z) {
      return x >= this.min.getX() && x <= this.max.getX() && y >= this.min.getY() && y <= this.max.getY() && z >= this.min.getZ() && z <= this.max.getZ();
   }

   public BlockPosition getMin() {
      return this.min;
   }

   public BlockPosition getMax() {
      return this.max;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getDepth() {
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
         return !(o instanceof BlockRegion other) ? false : this.getMin().equals(other.getMin()) && this.getMax().equals(other.getMax());
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
      return "BlockRegion(min=" + this.getMin() + ", max=" + this.getMax() + ")";
   }
}
