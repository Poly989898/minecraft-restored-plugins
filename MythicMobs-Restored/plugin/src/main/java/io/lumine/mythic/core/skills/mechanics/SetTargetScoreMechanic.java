package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
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
   name = "setTargetScore",
   aliases = {"sts"},
   description = "Sets a scoreboard value on the target entity"
)
public class SetTargetScoreMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private PlaceholderString objective;
   private PlaceholderInt value;

   public SetTargetScoreMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.objective = mlc.getPlaceholderString(new String[]{"objective", "obj", "o"}, "");
      this.value = mlc.getPlaceholderInteger(new String[]{"value", "v"}, 0);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
      Objective objective = scoreboard.getObjective(this.objective.get(data, target));
      if (objective == null) {
         objective = scoreboard.registerNewObjective(this.objective.get(data, target), "dummy");
      }

      if (target.isPlayer()) {
         objective.getScore(target.asPlayer().getName()).setScore(this.value.get(data, target));
      } else {
         objective.getScore(target.getUniqueId().toString()).setScore(this.value.get(data, target));
      }

      return SkillResult.SUCCESS;
   }
}
