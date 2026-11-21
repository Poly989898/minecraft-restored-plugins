package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "resetai",
   aliases = {"resetaigoals"},
   description = "Attempt to reset a mob's AI to default"
)
public class ResetAIMechanic extends SkillMechanic implements INoTargetSkill {
   public ResetAIMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      this.getPlugin().getVolatileCodeHandler().getAIHandler().resetAI(data.getCaster().getEntity());
      return SkillResult.SUCCESS;
   }
}
