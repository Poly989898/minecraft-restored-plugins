package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

@MythicMechanic(
   author = "Ashijin",
   name = "modifymobscore",
   aliases = {"mms"},
   description = ""
)
public class ModifyMobScoreMechanic extends ModifyGlobalScoreMechanic implements INoTargetSkill {
   public ModifyMobScoreMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
      String fObjective = super.objective.get(data);
      Objective objective = scoreboard.getObjective(fObjective);
      if (objective == null) {
         objective = scoreboard.registerNewObjective(fObjective, "dummy");
      }

      Score score = objective.getScore(data.getCaster().getEntity().getUniqueId().toString());
      score.setScore(this.applyModifier(score.getScore(), this.value.get(data)));
      return SkillResult.SUCCESS;
   }
}
