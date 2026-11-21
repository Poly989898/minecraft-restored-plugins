package io.lumine.mythic.api.skills;

import io.lumine.mythic.api.config.MythicLineConfig;

public interface MechanicProvider {
   default int getPriority() {
      return 0;
   }

   ISkillMechanic provideMechanic(String str, MythicLineConfig mythicLineConfig, ISkillMechanic iSkillMechanic);
}
