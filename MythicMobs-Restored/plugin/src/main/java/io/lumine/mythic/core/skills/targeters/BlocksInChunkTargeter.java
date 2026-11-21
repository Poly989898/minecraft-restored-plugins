package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

@MythicTargeter(
   author = "Ashijin",
   name = "blocksinchunk",
   aliases = {"BIC"},
   description = "Targets all blocks in the targeted chunk"
)
public class BlocksInChunkTargeter extends ILocationSelector {
   protected boolean origin = false;
   private BlocksInChunkTargeter.MaskType maskType;

   public BlocksInChunkTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      boolean noAir = mlc.getBoolean(new String[]{"noair", "na"}, true);
      boolean onlyAir = mlc.getBoolean(new String[]{"onlyair", "oa"}, false);
      this.origin = mlc.getBoolean(new String[]{"nearorigin", "no"}, false);
      if (noAir && !onlyAir) {
         this.maskType = BlocksInChunkTargeter.MaskType.IGNORE_AIR;
      } else if (onlyAir) {
         this.maskType = BlocksInChunkTargeter.MaskType.ONLY_AIR;
      } else {
         this.maskType = BlocksInChunkTargeter.MaskType.ALL;
      }
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      HashSet<AbstractLocation> targets = new HashSet<>();
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "@BlocksInChunk targeter");
      if (this.origin) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "@BlocksInChunk targeter from Origin");

         for (AbstractLocation location : this.getBlocksInRadius(data, data.getOrigin())) {
            targets.add(location);
         }
      } else if (data.getEntityTargets() != null && !data.getEntityTargets().isEmpty()) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "@BlocksInChunk targeter from Entities");

         for (AbstractEntity e : data.getEntityTargets()) {
            AbstractLocation l = e.getLocation();

            for (AbstractLocation location : this.getBlocksInRadius(data, l)) {
               targets.add(location);
            }
         }
      } else if (data.getLocationTargets() != null && !data.getLocationTargets().isEmpty()) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "@BlocksInChunk targeter from Locations");

         for (AbstractLocation l : data.getLocationTargets()) {
            for (AbstractLocation location : this.getBlocksInRadius(data, l)) {
               targets.add(location);
            }
         }
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "@BlocksInChunk has no inherited targets");
      }

      return targets;
   }

   private List<AbstractLocation> getBlocksInRadius(SkillMetadata data, AbstractLocation origin) {
      List<AbstractLocation> blocks = new ArrayList<>();
      Location o = BukkitAdapter.adapt(origin);
      Chunk chunk = o.getChunk();
      int minX = chunk.getX() << 4;
      int minZ = chunk.getZ() << 4;
      int maxX = minX | 15;
      int minY = chunk.getWorld().getMinHeight();
      int maxY = chunk.getWorld().getMaxHeight();
      int maxZ = minZ | 15;

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               AbstractLocation newloc = new AbstractLocation(origin.getWorld(), (double)x, (double)y, (double)z);
               if (this.maskType == BlocksInChunkTargeter.MaskType.ALL) {
                  blocks.add(newloc);
               } else if (this.maskType == BlocksInChunkTargeter.MaskType.IGNORE_AIR && BukkitAdapter.adapt(newloc).getBlock().getType() != Material.AIR) {
                  blocks.add(newloc);
               } else if (this.maskType == BlocksInChunkTargeter.MaskType.ONLY_AIR && BukkitAdapter.adapt(newloc).getBlock().getType() == Material.AIR) {
                  blocks.add(newloc);
               }
            }
         }
      }

      return blocks;
   }

   private static enum MaskType {
      ALL,
      IGNORE_AIR,
      ONLY_AIR;
   }
}
