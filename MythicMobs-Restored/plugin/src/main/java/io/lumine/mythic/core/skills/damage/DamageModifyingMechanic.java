package io.lumine.mythic.core.skills.damage;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import java.io.File;

public abstract class DamageModifyingMechanic extends SkillMechanic {
   public DamageModifyingMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
   }
}
