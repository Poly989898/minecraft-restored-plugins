package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ILocationComparisonCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "zdiff",
   aliases = {},
   description = "Whether the z-difference between the caster and target is within the given range"
)
public class ZDiffCondition extends SkillCondition implements IEntityComparisonCondition, ILocationComparisonCondition {
   @MythicField(
      name = "difference",
      aliases = {"diff", "d"},
      description = "The difference to match",
      defValue = "0"
   )
   protected RangedDouble diff;

   public ZDiffCondition(String line, MythicLineConfig mlc) {
      super(line);
      String d = mlc.getString(new String[]{"difference", "diff", "d"}, this.conditionVar);
      this.diff = new RangedDouble(d);
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractEntity target) {
      double t = target.getLocation().getZ();
      double c = entity.getLocation().getZ();
      return this.diff.equals(t - c);
   }

   @Override
   public boolean check(AbstractLocation location, AbstractLocation target) {
      double t = target.getZ();
      double c = location.getZ();
      return this.diff.equals(t - c);
   }
}
