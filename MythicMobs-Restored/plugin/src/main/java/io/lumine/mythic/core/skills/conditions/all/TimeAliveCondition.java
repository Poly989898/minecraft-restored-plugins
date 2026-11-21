package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedInt;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "timealive",
   aliases = {"alivetime"},
   description = "Tests how long since the entity was spawned"
)
public class TimeAliveCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "time",
      aliases = {"t"},
      description = "The time in ticks"
   )
   private RangedInt time;

   public TimeAliveCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.time = new RangedInt(mlc.getString(new String[]{"time", "t"}, this.conditionVar));
   }

   @Override
   public boolean check(AbstractEntity target) {
      return true;
   }
}
