package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setscore",
   description = "Sets a scoreboard value"
)
public class SetScoreMechanic extends ModifyScoreMechanic {
   public SetScoreMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.action = ModifyScoreMechanic.ModifyScoreAction.SET;
   }
}
