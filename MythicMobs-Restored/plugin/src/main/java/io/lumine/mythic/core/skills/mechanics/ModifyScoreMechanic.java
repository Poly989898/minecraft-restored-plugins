package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.OptionalInt;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

@MythicMechanic(
   author = "Ashijin",
   name = "modifyscore",
   description = "Modifies a scoreboard value"
)
public class ModifyScoreMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "entry",
      aliases = {"e", "name", "n"},
      defValue = "dummy",
      description = "The entry to modify in the scoreboard."
   )
   protected PlaceholderString entry;
   @MythicField(
      name = "objective",
      aliases = {"obj", "o"},
      defValue = "",
      description = "The objective in the scoreboard to modify."
   )
   protected PlaceholderString objective;
   @MythicField(
      name = "action",
      aliases = {"a"},
      defValue = "SET",
      description = "The action to perform for modifying the scoreboard value."
   )
   protected ModifyScoreMechanic.ModifyScoreAction action;
   @MythicField(
      name = "value",
      aliases = {"v"},
      defValue = "0",
      description = "The value to use for the modification."
   )
   protected PlaceholderString value;

   public ModifyScoreMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.entry = PlaceholderString.of(mlc.getString(new String[]{"entry", "e", "name", "n"}, "dummy"));
      this.objective = PlaceholderString.of(mlc.getString(new String[]{"objective", "obj", "o"}, ""));
      this.value = PlaceholderString.of(mlc.getString(new String[]{"value", "v"}, "0"));
      String strAction = mlc.getString(new String[]{"action", "a"}, "SET");

      try {
         this.action = ModifyScoreMechanic.ModifyScoreAction.valueOf(strAction.toUpperCase());
      } catch (Exception e) {
         this.action = ModifyScoreMechanic.ModifyScoreAction.SET;
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
      String fentry = this.entry.get(data);
      String fobjective = this.objective.get(data);
      OptionalInt value = Numbers.parseInteger(this.value.get(data));
      if (value.isEmpty()) {
         MythicLogger.errorMechanicConfig(this, this.config, "Scoreboard value must be an integer");
         return SkillResult.INVALID_CONFIG;
      } else {
         int fvalue = value.getAsInt();
         Objective objective = scoreboard.getObjective(fobjective);
         if (objective == null) {
            objective = scoreboard.registerNewObjective(fobjective, "dummy");
         }

         if (this.action != ModifyScoreMechanic.ModifyScoreAction.UNSET) {
            Score score = objective.getScore(fentry);
            score.setScore(this.applyModifier(score.getScore(), fvalue));
         }

         return SkillResult.SUCCESS;
      }
   }

   protected int applyModifier(int score, int value) {
      if (this.action == ModifyScoreMechanic.ModifyScoreAction.SET) {
         score = value;
      } else if (this.action == ModifyScoreMechanic.ModifyScoreAction.ADD) {
         score += value;
      } else if (this.action == ModifyScoreMechanic.ModifyScoreAction.SUBTRACT) {
         score -= value;
      } else if (this.action == ModifyScoreMechanic.ModifyScoreAction.MULTIPLY) {
         score *= value;
      } else if (this.action == ModifyScoreMechanic.ModifyScoreAction.DIVIDE) {
         score /= value;
      } else if (this.action == ModifyScoreMechanic.ModifyScoreAction.MOD) {
         score %= value;
      }

      return score;
   }

   protected static enum ModifyScoreAction {
      SET,
      ADD,
      SUBTRACT,
      MULTIPLY,
      DIVIDE,
      MOD,
      UNSET;
   }
}
