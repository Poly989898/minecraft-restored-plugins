package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;

@MythicTargeter(
   author = "Ashijin",
   name = "Console",
   aliases = {"None"},
   description = "Targets nothing"
)
public class ConsoleTargeter extends SkillTargeter {
   public ConsoleTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }
}
