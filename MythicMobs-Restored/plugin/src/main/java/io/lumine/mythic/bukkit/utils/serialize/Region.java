package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public final class Region implements GsonSerializable {
   private final Position min;
   private final Position max;
   private final double width;
   private final double height;
   private final double depth;

   public static Region deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("min"));
      Preconditions.checkArgument(object.has("max"));
      Position a = Position.deserialize(object.get("min"));
      Position b = Position.deserialize(object.get("max"));
      return of(a, b);
   }

   public static Region of(Position a, Position b) {
      Objects.requireNonNull(a, "a");
      Objects.requireNonNull(b, "b");
      if (!a.getWorld().equals(b.getWorld())) {
         throw new IllegalArgumentException("positions are in different worlds");
      } else {
         return new Region(a, b);
      }
   }

   private Region(Position a, Position b) {
      this.min = Position.of(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()), a.getWorld());
      this.max = Position.of(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()), a.getWorld());
      this.width = this.max.getX() - this.min.getX();
      this.height = this.max.getY() - this.min.getY();
      this.depth = this.max.getZ() - this.min.getZ();
   }

   public boolean inRegion(Position pos) {
      Objects.requireNonNull(pos, "pos");
      return pos.getWorld().equals(this.min.getWorld()) && this.inRegion(pos.getX(), pos.getY(), pos.getZ());
   }

   public boolean inRegion(Location loc) {
      Objects.requireNonNull(loc, "loc");
      return loc.getWorld().getName().equals(this.min.getWorld()) && this.inRegion(loc.getX(), loc.getY(), loc.getZ());
   }

   public boolean inRegion(double x, double y, double z) {
      int fx = Numbers.floor(x);
      int fy = Numbers.floor(y);
      int fz = Numbers.floor(z);
      return fx >= this.min.getX() && fx <= this.max.getX() && fy >= this.min.getY() && fy <= this.max.getY() && fz >= this.min.getZ() && fz <= this.max.getZ();
   }

   public boolean inRegion(Region region) {
      Objects.requireNonNull(region, "region");
      return this.inRegion(region.getMin()) && this.inRegion(region.getMax());
   }

   public boolean intersects(Region region) {
      Objects.requireNonNull(region, "region");
      Position selectionMin = this.getMin();
      Position selectionMax = this.getMax();
      Position regionMin = region.getMin();
      Position regionMax = region.getMax();
      return (
            regionMin.getX() <= selectionMax.getX() && regionMin.getX() >= selectionMin.getX()
               || regionMax.getX() <= selectionMax.getX() && regionMax.getX() >= selectionMin.getX()
               || selectionMin.getX() >= regionMin.getX() && selectionMin.getX() <= regionMax.getX()
               || selectionMax.getX() >= regionMin.getX() && selectionMax.getX() <= regionMax.getX()
         )
         && (
            regionMin.getY() <= selectionMax.getY() && regionMin.getY() >= selectionMin.getY()
               || regionMax.getY() <= selectionMax.getY() && regionMax.getY() >= selectionMin.getY()
               || selectionMin.getY() >= regionMin.getY() && selectionMin.getY() <= regionMax.getY()
               || selectionMax.getY() >= regionMin.getY() && selectionMax.getY() <= regionMax.getY()
         )
         && (
            regionMin.getZ() <= selectionMax.getZ() && regionMin.getZ() >= selectionMin.getZ()
               || regionMax.getZ() <= selectionMax.getZ() && regionMax.getZ() >= selectionMin.getZ()
               || selectionMin.getZ() >= regionMin.getZ() && selectionMin.getZ() <= regionMax.getZ()
               || selectionMax.getZ() >= regionMin.getZ() && selectionMax.getZ() <= regionMax.getZ()
         );
   }

   public Region shift(double x, double y, double z) {
      return new Region(this.min.add(x, y, z), this.max.add(x, y, z));
   }

   public Zone toZone() {
      return Zone.of(this.min.toLocus(), this.max.toLocus());
   }

   public Position getMin() {
      return this.min;
   }

   public Position getMax() {
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

   public Position center() {
      double centerX = (this.min.getX() + this.max.getX()) / 2.0;
      double centerY = (this.min.getY() + this.max.getY()) / 2.0;
      double centerZ = (this.min.getZ() + this.max.getZ()) / 2.0;
      return Position.of(centerX, centerY, centerZ, this.min.getWorld());
   }

   public Region expand(double x, double y, double z) {
      Position newMin = Position.of(
         this.min.getX() - (x < 0.0 ? Math.abs(x) : 0.0),
         this.min.getY() - (y < 0.0 ? Math.abs(y) : 0.0),
         this.min.getZ() - (z < 0.0 ? Math.abs(z) : 0.0),
         this.min.getWorld()
      );
      Position newMax = Position.of(
         this.max.getX() + (x > 0.0 ? x : 0.0), this.max.getY() + (y > 0.0 ? y : 0.0), this.max.getZ() + (z > 0.0 ? z : 0.0), this.max.getWorld()
      );
      return new Region(newMin, newMax);
   }

   public Region contract(double x, double y, double z) {
      double newMinX = this.min.getX() + (x > 0.0 ? x : 0.0);
      double newMinY = this.min.getY() + (y > 0.0 ? y : 0.0);
      double newMinZ = this.min.getZ() + (z > 0.0 ? z : 0.0);
      double newMaxX = this.max.getX() - (x < 0.0 ? Math.abs(x) : 0.0);
      double newMaxY = this.max.getY() - (y < 0.0 ? Math.abs(y) : 0.0);
      double newMaxZ = this.max.getZ() - (z < 0.0 ? Math.abs(z) : 0.0);
      if (!(newMaxX <= newMinX) && !(newMaxY <= newMinY) && !(newMaxZ <= newMinZ)) {
         Position newMin = Position.of(newMinX, newMinY, newMinZ, this.min.getWorld());
         Position newMax = Position.of(newMaxX, newMaxY, newMaxZ, this.max.getWorld());
         return new Region(newMin, newMax);
      } else {
         throw new IllegalArgumentException("Contracting by the specified amount would reduce the region to 0 or less in size.");
      }
   }

   public Region expandNorth(double amount) {
      return this.expand(0.0, 0.0, -amount);
   }

   public Region expandSouth(double amount) {
      return this.expand(0.0, 0.0, amount);
   }

   public Region expandEast(double amount) {
      return this.expand(amount, 0.0, 0.0);
   }

   public Region expandWest(double amount) {
      return this.expand(-amount, 0.0, 0.0);
   }

   public Region expandUp(double amount) {
      return this.expand(0.0, amount, 0.0);
   }

   public Region expandDown(double amount) {
      return this.expand(0.0, -amount, 0.0);
   }

   public Region contractNorth(double amount) {
      return this.contract(0.0, 0.0, amount);
   }

   public Region contractSouth(double amount) {
      return this.contract(0.0, 0.0, -amount);
   }

   public Region contractEast(double amount) {
      return this.contract(-amount, 0.0, 0.0);
   }

   public Region contractWest(double amount) {
      return this.contract(amount, 0.0, 0.0);
   }

   public Region contractUp(double amount) {
      return this.contract(0.0, -amount, 0.0);
   }

   public Region contractDown(double amount) {
      return this.contract(0.0, amount, 0.0);
   }

   public Region trim() {
      World world = Bukkit.getWorld(this.min.getWorld());
      int minX = (int)this.max.getX();
      int minY = (int)this.max.getY();
      int minZ = (int)this.max.getZ();
      int maxX = (int)this.min.getX();
      int maxY = (int)this.min.getY();
      int maxZ = (int)this.min.getZ();

      for (int x = (int)this.min.getX(); x <= (int)this.max.getX(); x++) {
         for (int y = (int)this.min.getY(); y <= (int)this.max.getY(); y++) {
            for (int z = (int)this.min.getZ(); z <= (int)this.max.getZ(); z++) {
               Block block = world.getBlockAt(x, y, z);
               if (block.getType() != Material.AIR) {
                  if (x < minX) {
                     minX = x;
                  }

                  if (y < minY) {
                     minY = y;
                  }

                  if (z < minZ) {
                     minZ = z;
                  }

                  if (x > maxX) {
                     maxX = x;
                  }

                  if (y > maxY) {
                     maxY = y;
                  }

                  if (z > maxZ) {
                     maxZ = z;
                  }
               }
            }
         }
      }

      Position newMin = Position.of((double)minX, (double)minY, (double)minZ, this.min.getWorld());
      Position newMax = Position.of((double)maxX, (double)maxY, (double)maxZ, this.min.getWorld());
      return new Region(newMin, newMax);
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
         return !(o instanceof Region other) ? false : this.getMin().equals(other.getMin()) && this.getMax().equals(other.getMax());
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
      return "Region(min=" + this.getMin() + ", max=" + this.getMax() + ")";
   }

   public Collection<Entity> getEntities() {
      return this.getEntities(null);
   }

   public Collection<Chunk> getChunks() {
      List<Chunk> chunks = new ArrayList<>();
      World world = Bukkit.getWorld(this.getMin().getWorld());
      int smallX = (int)(Math.floor(this.getMin().getX()) / 16.0);
      int bigX = (int)Math.floor(this.getMax().getX() / 16.0);
      int smallZ = (int)Math.floor(this.getMin().getZ() / 16.0);
      int bigZ = (int)Math.floor(this.getMax().getZ() / 16.0);

      for (int x = smallX; x <= bigX; x++) {
         for (int z = smallZ; z <= bigZ; z++) {
            Chunk chunk = world.getChunkAt(x, z);
            if (null != chunk) {
               chunks.add(chunk);
            }
         }
      }

      return chunks;
   }

   public void setChunksForceLoaded(boolean forceLoaded) {
      new ArrayList();
      World world = Bukkit.getWorld(this.getMin().getWorld());
      int smallX = (int)(Math.floor(this.getMin().getX()) / 16.0);
      int bigX = (int)Math.floor(this.getMax().getX() / 16.0);
      int smallZ = (int)Math.floor(this.getMin().getZ() / 16.0);
      int bigZ = (int)Math.floor(this.getMax().getZ() / 16.0);

      for (int x = smallX; x <= bigX; x++) {
         for (int z = smallZ; z <= bigZ; z++) {
            world.setChunkForceLoaded(x, z, forceLoaded);
         }
      }
   }

   public Collection<Entity> getEntities(Predicate<Entity> predicate) {
      List<Entity> entities = new ArrayList<>();
      World world = Bukkit.getWorld(this.getMin().getWorld());
      int smallX = (int)(Math.floor(this.getMin().getX()) / 16.0);
      int bigX = (int)Math.floor(this.getMax().getX() / 16.0);
      int smallZ = (int)Math.floor(this.getMin().getZ() / 16.0);
      int bigZ = (int)Math.floor(this.getMax().getZ() / 16.0);

      for (int x = smallX; x <= bigX; x++) {
         for (int z = smallZ; z <= bigZ; z++) {
            Chunk chunk = world.getChunkAt(x, z);
            if (null != chunk) {
               for (Entity entity : chunk.getEntities()) {
                  if (predicate == null || predicate.test(entity)) {
                     entities.add(entity);
                  }
               }
            }
         }
      }

      return entities;
   }

   public Collection<Block> getBlocks() {
      int minX = (int)Math.floor(this.getMin().getX());
      int minY = (int)Math.floor(this.getMin().getY());
      int minZ = (int)Math.floor(this.getMin().getZ());
      int maxX = (int)Math.floor(this.getMax().getX());
      int maxY = (int)Math.floor(this.getMax().getY());
      int maxZ = (int)Math.floor(this.getMax().getZ());
      List<Block> blocks = Lists.newArrayList();

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               blocks.add(new Location(this.getMax().toLocation().getWorld(), x, y, z).getBlock());
            }
         }
      }

      return blocks;
   }

   public Region transform2D(double angle, double aboutX, double aboutZ, double translateX, double translateZ) {
      Position pos1 = this.min.transform2D(angle, aboutX, aboutZ, translateX, translateZ);
      Position pos2 = this.max.transform2D(angle, aboutX, aboutZ, translateX, translateZ);
      return of(pos1, pos2);
   }
}
