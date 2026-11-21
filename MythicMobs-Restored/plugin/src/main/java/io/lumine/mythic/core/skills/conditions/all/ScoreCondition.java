package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

@MythicCondition(
   author = "Ashijin",
   name = "score",
   aliases = {},
   description = "Checks a scoreboard value of the target entity"
)
public class ScoreCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "objective",
      aliases = {"o"},
      description = "The objective"
   )
   private final PlaceholderString obj;
   @MythicField(
      name = "entry",
      aliases = {"e"},
      description = "The entry"
   )
   private final PlaceholderString ent;
   @MythicField(
      name = "value",
      aliases = {"v"},
      description = "The value to match"
   )
   private final PlaceholderString val;

   public ScoreCondition(String line, MythicLineConfig mlc) {
      super(line);
      String[] split = this.conditionVar.split(";");
      String val = split.length > 1 ? split[0] : "";
      String condObj = split.length > 2 ? split[1] : "";
      String condEnt = split.length > 3 ? split[2] : "";
      this.obj = mlc.getPlaceholderString(new String[]{"objective", "obj", "o"}, condObj);
      this.ent = mlc.getPlaceholderString(new String[]{"entry", "ent", "e"}, condEnt);
      this.val = mlc.getPlaceholderString(new String[]{"value", "val", "v"}, val);
   }

   @Override
   public boolean check(AbstractEntity target) {
      Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
      Objective objective = scoreboard.getObjective(this.obj.get(target));
      if (objective == null) {
         return new RangedDouble(this.val.get(target)).equals(0);
      } else {
         String entry = this.ent.get(target);
         int score;
         if (entry.isBlank()) {
            if (target.isPlayer()) {
               score = objective.getScore(target.asPlayer().getName()).getScore();
            } else {
               score = objective.getScore(target.getUniqueId().toString()).getScore();
            }
         } else {
            score = objective.getScore(entry).getScore();
         }

         return new RangedDouble(this.val.get(target)).equals(score);
      }
   }
}
