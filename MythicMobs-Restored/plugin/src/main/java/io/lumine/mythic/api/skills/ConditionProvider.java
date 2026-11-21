package io.lumine.mythic.api.skills;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ISkillCondition;

public interface ConditionProvider {
   default int getPriority() {
      return 0;
   }

   ISkillCondition provideCondition(String str, MythicLineConfig mythicLineConfig, ISkillCondition iSkillCondition);
}
