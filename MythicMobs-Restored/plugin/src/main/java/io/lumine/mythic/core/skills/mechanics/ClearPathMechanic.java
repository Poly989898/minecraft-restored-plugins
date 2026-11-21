package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Archontas",
   name = "clearpath",
   aliases = {"stoppath", "clearnavigation", "stopnavigation"},
   description = "Clears the target's current navigation path."
)
public class ClearPathMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public ClearPathMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isMob()) {
         return SkillResult.INVALID_TARGET;
      } else {
         this.getPlugin().getVolatileCodeHandler().getAIHandler().clearPath(target);
         return SkillResult.SUCCESS;
      }
   }
}
