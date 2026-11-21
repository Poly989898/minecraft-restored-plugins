package io.lumine.mythic.bukkit.utils.folia;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class FoliaHelper {
   public static boolean isOwnedByCurrentRegion(Entity entity) {
      return Bukkit.isOwnedByCurrentRegion(entity);
   }

   public static boolean isOwnedByCurrentRegion(Location location) {
      return Bukkit.isOwnedByCurrentRegion(location);
   }
}
