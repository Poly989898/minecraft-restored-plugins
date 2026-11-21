package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.WorldGuardSupport;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.Optional;
import org.bukkit.World;

@MythicTargeter(
   author = "Ashijin",
   name = "chunksInWERegion",
   aliases = {"chunksInWGRegion"},
   description = "Targets the 0,0 chunk corners in a region"
)
public class ChunksInWERegionTargeter extends ILocationSelector {
   protected PlaceholderString region;

   public ChunksInWERegionTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.region = mlc.getPlaceholderString(new String[]{"region", "r"}, null);
      if (this.region == null) {
         MythicLogger.errorTargeterConfig(this, mlc, "Region name must be set.");
      }
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = Lists.newArrayList();
      Optional<WorldGuardSupport> maybeWG = this.getPlugin().getCompatibility().getWorldGuard();
      if (maybeWG.isEmpty()) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "WorldGuard compatibility unavailable");
         return targets;
      } else {
         WorldGuardPlatform wg = WorldGuard.getInstance().getPlatform();
         AbstractWorld world = data.getCaster().getLocation().getWorld();
         World bukkitWorld = BukkitAdapter.adapt(world);
         com.sk89q.worldedit.world.World weWorld = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(bukkitWorld);
         RegionManager regionManager = wg.getRegionContainer().get(weWorld);
         String regionName = this.region.get(data).toLowerCase();
         ProtectedRegion region = (ProtectedRegion)regionManager.getRegions().get(regionName);
         if (region == null) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "WorldGuard region not found");
            return targets;
         } else {
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();
            int minChunkX = min.getBlockX() >> 4;
            int minChunkZ = min.getBlockZ() >> 4;
            int maxChunkX = max.getBlockX() >> 4;
            int maxChunkZ = max.getBlockZ() >> 4;

            for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
               for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                  int baseX = chunkX << 4;
                  int baseZ = chunkZ << 4;
                  AbstractLocation chunkBaseLocation = new AbstractLocation(world, (double)baseX, 0.0, (double)baseZ);
                  targets.add(chunkBaseLocation);
               }
            }

            return targets;
         }
      }
   }
}
