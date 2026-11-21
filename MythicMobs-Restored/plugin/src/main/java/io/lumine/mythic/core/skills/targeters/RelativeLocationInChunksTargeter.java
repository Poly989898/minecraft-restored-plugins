package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import org.bukkit.Chunk;
import org.bukkit.World;

@MythicTargeter(
   author = "Ashijin",
   name = "relativeLocationInLoadedChunks",
   aliases = {"relLocationInLoadedChunks"},
   description = "Targets the same relative position as the target in every loaded chunk"
)
public class RelativeLocationInChunksTargeter extends ILocationSelector {
   public RelativeLocationInChunksTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = Lists.newArrayList();
      AbstractLocation origin = data.getOrigin();
      World world = BukkitAdapter.adapt(origin.getWorld());
      if (world == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Origin world is null");
         return targets;
      } else {
         for (AbstractLocation originalLocation : data.getLocationTargets()) {
            int originChunkX = originalLocation.getBlockX() >> 4;
            int originChunkZ = originalLocation.getBlockZ() >> 4;
            int relativeX = originalLocation.getBlockX() & 15;
            int relativeZ = originalLocation.getBlockZ() & 15;

            for (Chunk chunk : world.getLoadedChunks()) {
               int chunkX = chunk.getX() << 4;
               int chunkZ = chunk.getZ() << 4;
               int targetX = chunkX + relativeX;
               int targetZ = chunkZ + relativeZ;
               AbstractLocation targetLocation = new AbstractLocation(
                  originalLocation.getWorld(), (double)targetX, (double)originalLocation.getBlockY(), (double)targetZ
               );
               targets.add(targetLocation);
            }
         }

         return targets;
      }
   }
}
