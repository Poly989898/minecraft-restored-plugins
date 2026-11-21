package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.Location;
import org.bukkit.World;

public final class BlockOrient implements GsonSerializable {
   private final BlockLocus locus;
   private final Direction direction;

   public static BlockOrient deserialize(String element) {
      return deserialize(element, of(0, 0, 0, 0.0F, 0.0F));
   }

   public static BlockOrient deserialize(String element, BlockOrient def) {
      Preconditions.checkNotNull(element);
      String[] values = element.split(",");
      int x = Integer.parseInt(values[0]);
      int y = Integer.parseInt(values[1]);
      int z = Integer.parseInt(values[2]);
      float yaw = values.length > 3 ? Float.parseFloat(values[3]) : def.getDirection().getYaw();
      float pitch = values.length > 4 ? Float.parseFloat(values[4]) : def.getDirection().getPitch();
      return of(x, y, z, yaw, pitch);
   }

   public static BlockOrient deserialize(JsonElement element) {
      BlockLocus locus = BlockLocus.deserialize(element);
      Direction direction = Direction.deserialize(element);
      return of(locus, direction);
   }

   public static BlockOrient of(BlockLocus locus, Direction direction) {
      Objects.requireNonNull(locus, "locus");
      Objects.requireNonNull(direction, "direction");
      return new BlockOrient(locus, direction);
   }

   public static BlockOrient of(Location location) {
      Objects.requireNonNull(location, "location");
      return of(BlockLocus.of(location), Direction.from(location));
   }

   public static BlockOrient of(int x, int y, int z, float yaw, float pitch) {
      return of(BlockLocus.of(x, y, z), Direction.of(yaw, pitch));
   }

   private BlockOrient(BlockLocus locus, Direction direction) {
      this.locus = locus;
      this.direction = direction;
   }

   public BlockLocus getLocus() {
      return this.locus;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public synchronized Location toLocation(World world) {
      return new Location(world, this.locus.getX(), this.locus.getY(), this.locus.getZ(), this.direction.getYaw(), this.direction.getPitch());
   }

   public synchronized Location toCenteredLocation(World world) {
      return new Location(world, this.locus.getX() + 0.5, this.locus.getY(), this.locus.getZ() + 0.5, this.direction.getYaw(), this.direction.getPitch());
   }

   public BlockOrient add(int x, int y, int z) {
      return this.locus.add(x, y, z).withDirection(this.direction);
   }

   public BlockOrient subtract(int x, int y, int z) {
      return this.locus.subtract(x, y, z).withDirection(this.direction);
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().addAll(this.locus.serialize()).addAll(this.direction.serialize()).build();
   }

   public String serializeShortForm() {
      return this.locus.serializeShortForm() + "," + this.direction.serializeShortForm();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof BlockOrient other) ? false : this.getLocus().equals(other.getLocus()) && this.getDirection().equals(other.getDirection());
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getLocus().hashCode();
      return result * 59 + this.getDirection().hashCode();
   }

   @Override
   public String toString() {
      return "BlockOrient(locus=" + this.getLocus() + ", direction=" + this.getDirection() + ")";
   }
}
