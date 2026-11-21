package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "serverispaper",
   aliases = {"ispaper"},
   description = "Whether or not the server is running a fork of paper"
)
public class ServerIsPaperCondition extends SkillCondition implements ICasterCondition {
   public ServerIsPaperCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(SkillCaster caster) {
      return ServerVersion.isPaper();
   }
}
