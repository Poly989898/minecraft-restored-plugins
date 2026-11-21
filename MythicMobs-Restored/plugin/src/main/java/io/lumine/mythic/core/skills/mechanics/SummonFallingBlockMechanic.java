package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;

@MythicMechanic(
   author = "Ashijin",
   name = "summonFallingBlock",
   description = "Summons a falling block"
)
public class SummonFallingBlockMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   private PlaceholderString blockType;
   private AbstractBlock block = null;

   public SummonFallingBlockMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.blockType = mlc.getPlaceholderString(new String[]{"types", "type", "t", "material", "mat", "m"}, "DIRT");
      if (this.blockType.isStatic()) {
         this.getPlugin().getClock().queuePostReload(() -> {
            AbstractBlock block = this.getPlugin().getBootstrap().getBlock(this.blockType.get());
            if (block instanceof AbstractBlock.InvalidBlock) {
               MythicLogger.errorMechanicConfig(this, mlc, "'" + this.blockType.get() + "' is not a valid block type.");
            } else {
               this.block = block;
            }
         });
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Location location = BukkitAdapter.adapt(target);
      FallingBlock block = location.getWorld().spawnFallingBlock(location, this.block.getBlockData());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.castAtLocation(data, target.getLocation());
      return SkillResult.SUCCESS;
   }
}
