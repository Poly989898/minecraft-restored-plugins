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
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setBlockType",
   aliases = {"setBlock"},
   description = "Sets the target location to the given block type"
)
public class SetBlockTypeMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "types",
      aliases = {"type", "t", "material", "mat", "m", "block", "b"},
      description = "The block type to set at the target location",
      defValue = "DIRT"
   )
   private PlaceholderString blockType;
   @MythicField(
      name = "physics",
      aliases = {"p"},
      description = "Whether block updates/physics should be applied",
      defValue = "true"
   )
   private boolean physics;
   private AbstractBlock block = null;

   public SetBlockTypeMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.blockType = mlc.getPlaceholderString(new String[]{"types", "type", "t", "material", "mat", "m", "block", "b"}, "DIRT");
      this.physics = mlc.getBoolean(new String[]{"physics", "p"}, true);
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
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).entity(target).build();
      return this.cast(context);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();
      return this.cast(context);
   }

   public SkillResult cast(PlaceholderContext context) {
      if (this.block != null) {
         if (this.block instanceof AbstractBlock.InvalidBlock) {
            MythicLogger.errorMechanicConfig(this, this.getConfig(), "'" + this.blockType.get() + "' is not a valid block type.");
            return SkillResult.INVALID_CONFIG;
         }

         this.block.set(context.location(), this.physics);
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Setting block to {0}", this.block.toString());
      } else {
         String type = this.blockType.get(context);
         AbstractBlock block = this.getPlugin().getBootstrap().getBlock(type);
         if (block instanceof AbstractBlock.InvalidBlock) {
            MythicLogger.errorMechanicConfig(this, this.getConfig(), "'" + type + "' is not a valid block type.");
            return SkillResult.INVALID_CONFIG;
         }

         block.set(context.location(), this.physics);
      }

      return SkillResult.SUCCESS;
   }
}
