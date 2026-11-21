package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

@MythicCondition(
   author = "Ashijin",
   name = "globalscore",
   aliases = {"scoreglobal"},
   description = "Checks a global scoreboard value"
)
public class ScoreGlobalCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "objective",
      aliases = {"o"},
      description = "The objective"
   )
   private final PlaceholderString obj;
   @MythicField(
      name = "value",
      aliases = {"v"},
      description = "The value to match"
   )
   private final PlaceholderDouble val;

   public ScoreGlobalCondition(String line, MythicLineConfig mlc) {
      super(line);
      String val = "";
      String condObj = "";
      if (this.conditionVar.split(";").length > 2) {
         String[] split = this.conditionVar.split(";");
         condObj = split[0];
         val = split[1];
      }

      this.obj = mlc.getPlaceholderString(new String[]{"objective", "obj", "o"}, condObj);
      this.val = mlc.getPlaceholderDouble(new String[]{"value", "val", "v"}, val);
   }

   @Override
   public boolean check(AbstractEntity target) {
      Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
      Objective objective = scoreboard.getObjective(this.obj.get(target));
      if (objective == null) {
         objective = scoreboard.registerNewObjective(this.obj.get(target), "dummy");
      }

      int score = objective.getScore("__GLOBAL__").getScore();
      return this.val.get(target) == score;
   }
}
