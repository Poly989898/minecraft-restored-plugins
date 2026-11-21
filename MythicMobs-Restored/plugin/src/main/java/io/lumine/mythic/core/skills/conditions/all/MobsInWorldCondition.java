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
   name = "mobsinworld",
   aliases = {},
   description = "Matches a range to how many mobs are in the target world"
)
public class MobsInWorldCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The number range to match",
      defValue = "0"
   )
   private final RangedDouble amount;

   public MobsInWorldCondition(String line, MythicLineConfig mlc) {
      super(line);
      String k = mlc.getString(new String[]{"amount", "a"}, "0", this.conditionVar);
      this.amount = new RangedDouble(k);
   }

   @Override
   public boolean check(AbstractLocation l) {
      return this.amount.equals(l.getWorld().getLivingEntities().size());
   }
}
