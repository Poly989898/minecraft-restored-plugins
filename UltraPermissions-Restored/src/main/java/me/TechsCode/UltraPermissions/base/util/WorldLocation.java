package me.TechsCode.UltraPermissions.base.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

public class WorldLocation extends Location {
   private final String worldName;

   public static String serialize(WorldLocation worldLocation) {
      return worldLocation.getWorldName() + ";" + worldLocation.getBlockX() + ";" + worldLocation.getBlockY() + ";" + worldLocation.getBlockZ();
   }

   public static WorldLocation deserialize(String text) {
      String[] texts = text.split(";");
      return new WorldLocation(texts[0], Integer.parseInt(texts[1]), Integer.parseInt(texts[2]), Integer.parseInt(texts[3]));
   }

   public static WorldLocation toObj(Location location) {
      if (location.getWorld() == null) {
         throw new IllegalArgumentException("Location world cannot be null when converting to ShopLocation");
      } else {
         return new WorldLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
      }
   }

   public WorldLocation(@NonNls String text, double value, double y, double z) {
      super(Bukkit.getWorld(text), value, y, z);
      this.worldName = text;
   }

   public boolean blockEqual(Location location) {
      if (location.getWorld() != null && this.getWorld() != null) {
         return !location.getWorld().equals(this.getWorld())
            ? false
            : location.getBlockX() == this.getBlockX() && location.getBlockY() == this.getBlockY() && location.getBlockZ() == this.getBlockZ();
      } else {
         return false;
      }
   }

   @Nullable
   public World getWorld() {
      World world = super.getWorld();
      if (world == null) {
         super.setWorld(Bukkit.getWorld(this.worldName));
      }

      return super.getWorld();
   }

   public String getWorldName() {
      return this.worldName;
   }
}
