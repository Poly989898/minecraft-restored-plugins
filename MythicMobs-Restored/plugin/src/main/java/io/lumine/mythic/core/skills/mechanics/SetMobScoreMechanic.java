package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

@MythicMechanic(
   author = "Ashijin",
   name = "setMobScore",
   aliases = {"sms"},
   description = "Sets a scoreboard value on the caster"
)
public class SetMobScoreMechanic extends SkillMechanic implements INoTargetSkill {
   private PlaceholderString objective;
   private PlaceholderInt value;

   public SetMobScoreMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.objective = mlc.getPlaceholderString(new String[]{"objective", "obj", "o"}, "");
      this.value = mlc.getPlaceholderInteger(new String[]{"value", "v"}, 0);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
      Objective objective = scoreboard.getObjective(this.objective.get(data));
      if (objective == null) {
         objective = scoreboard.registerNewObjective(this.objective.get(data), "dummy");
      }

      objective.getScore(data.getCaster().getEntity().getUniqueId().toString()).setScore(this.value.get(data));
      return SkillResult.SUCCESS;
   }
}
