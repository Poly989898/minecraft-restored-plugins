package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "localdifficulty",
   aliases = {},
   description = "Tests the difficulty scale at the target location"
)
public class LocalDifficultyCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "difficulty",
      aliases = {"diff", "d"},
      description = "The difficulty range to check"
   )
   private final RangedDouble difficulty;

   public LocalDifficultyCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.difficulty = new RangedDouble(mlc.getString(new String[]{"difficulty", "diff", "d"}, this.conditionVar));
   }

   @Override
   public boolean check(AbstractLocation target) {
      float diff = getPlugin().getVolatileCodeHandler().getWorldHandler().getDifficultyScale(target);
      return this.difficulty.equals(diff);
   }
}
