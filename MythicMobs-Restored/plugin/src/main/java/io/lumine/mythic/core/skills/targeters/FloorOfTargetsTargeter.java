package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

@MythicTargeter(
   author = "Ashijin",
   name = "floorOfTargets",
   aliases = {"floorsOfTarget", "FOT"},
   description = "Targets the first non-passable block up to <tries> blocks below each target"
)
public class FloorOfTargetsTargeter extends ILocationSelector {
   private final int tries;

   public FloorOfTargetsTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.tries = mlc.getInteger(new String[]{"tries", "t", "max", "m"}, 3);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = new HashSet<>();
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "@FOT start (tries={0})", this.tries);
      if (data.getEntityTargets() != null) {
         for (AbstractEntity ent : data.getEntityTargets()) {
            AbstractLocation floor = this.findFloor(ent.getBukkitEntity().getLocation());
            if (floor != null) {
               AbstractLocation mutated = this.mutate(data, floor);
               targets.add(mutated);
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "@FOT entity '{0}' \u2192 {1}", ent.getName(), mutated);
            }
         }
      }

      if (data.getLocationTargets() != null) {
         for (AbstractLocation loc : data.getLocationTargets()) {
            Location bLoc = BukkitAdapter.adapt(loc);
            AbstractLocation floor = this.findFloor(bLoc);
            if (floor != null) {
               AbstractLocation mutated = this.mutate(data, floor);
               targets.add(mutated);
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "@FOT location {0} \u2192 {1}", bLoc, mutated);
            }
         }
      }

      return targets;
   }

   private AbstractLocation findFloor(Location start) {
      World world = start.getWorld();
      int x = start.getBlockX();
      int y = start.getBlockY();
      int z = start.getBlockZ();

      for (int i = 1; i <= this.tries; i++) {
         Block block = world.getBlockAt(x, y - i, z);
         if (!block.isPassable()) {
            Location onTop = block.getLocation().add(0.5, 0.0, 0.5);
            return BukkitAdapter.adapt(onTop);
         }
      }

      return null;
   }
}
