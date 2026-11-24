package me.TechsCode.UltraPermissions.base.legacy.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class LocationUtils {
   public LocationUtils() {
   }

   public static Location getValidSpawnPoint(Location location) {
      return getValidSpawnPoint(location, 1);
   }

   private static Location getValidSpawnPoint(Location location, int count) {
      for (int i = location.getBlockX() - count; i <= location.getBlockX() + count; i++) {
         for (int j = location.getBlockY() - count; j <= location.getBlockY() + count; j++) {
            for (int k = location.getBlockZ() - count; k <= location.getBlockZ() + count; k++) {
               Location candidate = new Location(location.getWorld(), i + 0.5, j, k + 0.5);
               Location above = candidate.clone().add(0.0, 1.0, 0.0);
               if (candidate.getBlock().getType() == Material.AIR && above.getBlock().getType() == Material.AIR) {
                  return candidate;
               }
            }
         }
      }

      return getValidSpawnPoint(location, count + 1);
   }

   public static String serialize(Location location) {
      return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
   }

   public static Location deserialize(String text) {
      String[] texts = text.split(";");
      return new Location(Bukkit.getWorld(texts[0]), Integer.parseInt(texts[1]), Integer.parseInt(texts[2]), Integer.parseInt(texts[3]));
   }
}
