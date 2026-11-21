package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

@MythicTargeter(
   author = "Ashijin",
   name = "escapeLocation",
   aliases = {"EndermanTeleport"},
   description = "Targets a nearby safe escape location like how an enderman teleports"
)
public class EscapeLocationTargeter extends ILocationSelector {
   private final PlaceholderDouble searchRadius;
   private final PlaceholderInt searchIterations;
   private final PlaceholderInt entityHeight;

   public EscapeLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.searchRadius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, 32.0);
      this.searchIterations = mlc.getPlaceholderInteger(new String[]{"iterations", "i"}, 64);
      this.entityHeight = mlc.getPlaceholderInteger(new String[]{"height", "h"}, 3);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster caster = data.getCaster();
      ArrayList<AbstractLocation> targets = new ArrayList<>();
      double radius = Math.max(1.0, this.searchRadius.get(data));
      int iterations = Math.max(1, this.searchIterations.get(data));
      int height = Math.max(1, this.entityHeight.get(data));
      AbstractLocation origin = caster.getLocation();
      AbstractLocation escape = this.findEscapeLocation(origin, radius, iterations, height);
      if (escape == null) {
         targets.add(this.mutate(data, caster.getEntity().getLocation()));
      } else {
         targets.add(this.mutate(data, escape));
      }

      return targets;
   }

   private AbstractLocation findEscapeLocation(AbstractLocation origin, double radius, int iterations, int height) {
      Location bukkitOrigin = BukkitAdapter.adapt(origin);
      World world = bukkitOrigin.getWorld();
      if (world == null) {
         return null;
      } else {
         ThreadLocalRandom random = ThreadLocalRandom.current();
         int minY = world.getMinHeight() + 1;
         int maxY = world.getMaxHeight() - height;

         for (int i = 0; i < iterations; i++) {
            double dx = (random.nextDouble() - 0.5) * 2.0 * radius;
            double dy = (random.nextDouble() - 0.5) * 2.0 * radius;
            double dz = (random.nextDouble() - 0.5) * 2.0 * radius;
            double x = bukkitOrigin.getX() + dx;
            double y = bukkitOrigin.getY() + dy;
            double z = bukkitOrigin.getZ() + dz;
            int bx = (int)Math.floor(x);
            int by = (int)Math.floor(y);
            int bz = (int)Math.floor(z);
            if (by >= minY && by <= maxY && this.isSafeSpot(world, bx, by, bz, height)) {
               Location target = new Location(world, x, y, z, bukkitOrigin.getYaw(), bukkitOrigin.getPitch());
               return BukkitAdapter.adapt(target);
            }
         }

         return null;
      }
   }

   private boolean isSafeSpot(World world, int x, int y, int z, int height) {
      Block below = world.getBlockAt(x, y - 1, z);
      if (below.getType().isSolid() && !below.isLiquid()) {
         for (int dy = 0; dy < height; dy++) {
            Block space = world.getBlockAt(x, y + dy, z);
            if (space.getType().isSolid() || space.isLiquid()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
