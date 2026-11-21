package io.lumine.mythic.bukkit.utils.particles;

import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import io.lumine.mythic.bukkit.utils.serialize.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class Particles {
   public static final void drawRegion2D(Player to, Region region, ParticleBuilder particle) {
      double minX = region.getMin().getX();
      double minZ = region.getMin().getZ();
      double maxX = region.getMax().getX();
      double maxZ = region.getMax().getZ();

      for (double x = minX; x <= maxX; x++) {
         for (double z = minZ; z <= maxZ; z++) {
            if (x == minX || x == maxX || z == minZ || z == maxZ) {
               particle.send(to, new Location(to.getWorld(), x + 0.5, to.getLocation().getY(), z + 0.5));
            }
         }
      }
   }

   public static final void drawRegion3D(Player to, Region region, ParticleBuilder particle) {
      double minX = region.getMin().getX() >= 0.0 ? region.getMin().getX() + 0.5 : region.getMin().getX() - 0.5;
      double minY = region.getMin().getY() >= 0.0 ? region.getMin().getY() + 0.5 : region.getMin().getY() - 0.5;
      double minZ = region.getMin().getZ() >= 0.0 ? region.getMin().getZ() + 0.5 : region.getMin().getZ() - 0.5;
      double maxX = region.getMax().getX() >= 0.0 ? region.getMax().getX() + 0.5 : region.getMax().getX() - 0.5;
      double maxY = region.getMax().getY() >= 0.0 ? region.getMax().getY() + 0.5 : region.getMax().getY() - 0.5;
      double maxZ = region.getMax().getZ() >= 0.0 ? region.getMax().getZ() + 0.5 : region.getMax().getZ() - 0.5;

      for (double x = minX; x <= maxX; x++) {
         for (double z = minZ; z <= maxZ; z++) {
            for (double y = minY; y <= maxY; y++) {
               if (x == minX || x == maxX || z == minZ || z == maxZ || y == minY || y == maxY) {
                  particle.send(to, new Location(to.getWorld(), x, y, z));
               }
            }
         }
      }
   }

   public static final void drawOutline3D(Player to, Region region, ParticleBuilder particle) {
      Position min = region.getMin();
      Position max = region.getMax().add(1.0, 1.0, 1.0);
      Position b2 = Position.of(max.getX(), min.getY(), min.getZ(), to.getWorld());
      Position b3 = Position.of(min.getX(), min.getY(), max.getZ(), to.getWorld());
      Position b4 = Position.of(max.getX(), min.getY(), max.getZ(), to.getWorld());
      Position t2 = Position.of(max.getX(), max.getY(), min.getZ(), to.getWorld());
      Position t3 = Position.of(min.getX(), max.getY(), max.getZ(), to.getWorld());
      Position t4 = Position.of(min.getX(), max.getY(), min.getZ(), to.getWorld());
      drawLine(to, min, b2, particle);
      drawLine(to, min, b3, particle);
      drawLine(to, b4, b2, particle);
      drawLine(to, b4, b3, particle);
      drawLine(to, min, t4, particle);
      drawLine(to, b2, t2, particle);
      drawLine(to, b3, t3, particle);
      drawLine(to, b4, max, particle);
      drawLine(to, max, t2, particle);
      drawLine(to, max, t3, particle);
      drawLine(to, t4, t2, particle);
      drawLine(to, t4, t3, particle);
   }

   public static final void drawLine(Player to, Position pos1, Position pos2, ParticleBuilder particle) {
      drawLine(to, pos1.toLocation(), pos2.toLocation(), particle);
   }

   public static final void drawLine(Player to, Location pos1, Location pos2, ParticleBuilder particle) {
      if (pos1.getWorld().equals(pos2.getWorld())) {
         double distance = pos1.distance(pos2);
         int points = Numbers.floor(distance);
         double gap = distance / points;
         Vector add = pos1.toVector().subtract(pos2.toVector()).normalize().multiply(gap);
         Location start = pos1.clone();

         for (int i = 0; i <= points; i++) {
            particle.send(to, start);
            start = start.subtract(add);
         }
      }
   }
}
