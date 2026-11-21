package io.lumine.mythic.core.utils;

import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.skills.projectiles.ProjectileSurfaceMode;
import java.util.HashSet;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;

public class BlockUtil {
   private static final HashSet<Material> PATHABLE = new HashSet<>();
   private static final HashSet<Material> MAYBE_PATHABLE = new HashSet<>();
   private static final HashSet<Material> BREATHABLE = new HashSet<>();

   public static boolean isPathable(Block block) {
      return isPathable(block.getType());
   }

   public static boolean isPathable(Block block, Projectile.ProjectileTracker projectile) {
      if (block.isPassable()) {
         return true;
      } else {
         BoundingBox bb = projectile.getHitbox();
         if (bb == null) {
            return true;
         } else {
            boolean overlap = bb.overlaps(block.getBoundingBox());
            return !overlap;
         }
      }
   }

   public static boolean isPathable(Block block, ProjectileSurfaceMode surfaceMode) {
      return surfaceMode != ProjectileSurfaceMode.WATER || block.getType() != Material.WATER && block.getType() != Material.LAVA ? block.isPassable() : false;
   }

   public static boolean isPathable(Material material) {
      return PATHABLE.contains(material);
   }

   public static boolean isBreathable(Block block) {
      return isBreathable(block.getType());
   }

   public static boolean isBreathable(Material material) {
      return !material.isSolid() && material != Material.WATER && material != Material.LAVA;
   }

   static {
      for (Material material : Material.values()) {
         if (!material.toString().equals("GRASS_PATH") && !material.toString().equals("DIRT_PATH")) {
            if (!material.isOccluding()) {
               PATHABLE.add(material);
               MAYBE_PATHABLE.add(material);
            } else if (material.toString().contains("SLAB")) {
               PATHABLE.add(material);
               MAYBE_PATHABLE.add(material);
            }
         }
      }
   }
}
