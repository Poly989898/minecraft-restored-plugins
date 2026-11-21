package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

@MythicMechanic(
   author = "Ashijin",
   name = "modifytargetscore",
   aliases = {"mts"},
   description = ""
)
public class ModifyTargetScoreMechanic extends SkillMechanic implements ITargetedEntitySkill {
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
      defValue = "ADD",
      description = "The action to perform for modifying the target entity's score."
   )
   protected ModifyGlobalScoreMechanic.ModifyScoreAction action;
   @MythicField(
      name = "value",
      aliases = {"v"},
      description = "The value to use for the modification."
   )
   protected PlaceholderString value;

   public ModifyTargetScoreMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.objective = PlaceholderString.of(mlc.getString(new String[]{"objective", "obj", "o"}, ""));
      this.value = PlaceholderString.of(mlc.getString(new String[]{"value", "v"}));
      String strAction = mlc.getString(new String[]{"action", "a"}, "ADD");

      try {
         this.action = ModifyGlobalScoreMechanic.ModifyScoreAction.valueOf(strAction.toUpperCase());
      } catch (Exception e) {
         this.action = ModifyGlobalScoreMechanic.ModifyScoreAction.ADD;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
      String fobjective = this.objective.get(data, target);
      int fvalue = Integer.valueOf(this.value.get(data, target));
      Objective objective = scoreboard.getObjective(fobjective);
      if (objective == null) {
         objective = scoreboard.registerNewObjective(fobjective, "dummy");
      }

      Score score;
      if (target.isPlayer()) {
         score = objective.getScore(target.asPlayer().getName());
      } else {
         score = objective.getScore(target.getUniqueId().toString());
      }

      score.setScore(this.applyModifier(score.getScore(), fvalue));
      return SkillResult.SUCCESS;
   }

   protected int applyModifier(int score, int value) {
      if (this.action == ModifyGlobalScoreMechanic.ModifyScoreAction.SET) {
         score = value;
      } else if (this.action == ModifyGlobalScoreMechanic.ModifyScoreAction.ADD) {
         score += value;
      } else if (this.action == ModifyGlobalScoreMechanic.ModifyScoreAction.SUBTRACT) {
         score -= value;
      } else if (this.action == ModifyGlobalScoreMechanic.ModifyScoreAction.MULTIPLY) {
         score *= value;
      } else if (this.action == ModifyGlobalScoreMechanic.ModifyScoreAction.DIVIDE) {
         score /= value;
      } else if (this.action == ModifyGlobalScoreMechanic.ModifyScoreAction.MOD) {
         score %= value;
      }

      return score;
   }
}
