package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IDummySkill;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Phil",
   name = "cancelSkill",
   aliases = {"cancel", "return"},
   description = "Delays the execution of the next mechanic"
)
public class CancelSkill extends SkillMechanic implements IDummySkill {
   public CancelSkill(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
   }
}
