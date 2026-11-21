package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Entity;

@MythicMechanic(
   name = "setSulfurCubeArchetype",
   description = "Sets the archetype of the target sulfur cube"
)
public class SetSulfurCubeArchetypeMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "archetype",
      aliases = {"a"},
      description = "The archetype to set on the target sulfur cube"
   )
   private final PlaceholderString archetype;

   public SetSulfurCubeArchetypeMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.archetype = mlc.getPlaceholderString(new String[]{"archetype", "a"}, null);
      if (this.archetype == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'archetype' attribute is required.");
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.archetype == null) {
         return SkillResult.INVALID_CONFIG;
      } else {
         Entity bukkitTarget = target.getBukkitEntity();
         String archetype = this.archetype.get(data, target);
         Schedulers.runOnRegion(
            bukkitTarget, () -> this.getPlugin().getVolatileCodeHandler().getCompatibilityHandler().setSulfurCubeArchetype(bukkitTarget, archetype)
         );
         return SkillResult.SUCCESS;
      }
   }
}
