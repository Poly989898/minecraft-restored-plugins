package net.Indyuce.mmocore.util;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class HashableLocation {
   private final World world;
   private final int x;
   private final int y;
   private final int z;

   public HashableLocation(@NotNull Location loc) {
      this.world = loc.getWorld();
      this.x = loc.getBlockX();
      this.y = loc.getBlockY();
      this.z = loc.getBlockZ();
   }

   public World getWorld() {
      return this.world;
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

   public Location bukkit() {
      return new Location(this.world, this.x, this.y, this.z);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         HashableLocation that = (HashableLocation)o;
         return this.x == that.x && this.y == that.y && this.z == that.z && this.world.equals(that.world);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.world, this.x, this.y, this.z);
   }
}
