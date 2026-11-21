package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "level",
   aliases = {},
   description = "Checks the target MythicMob's level"
)
public class LevelCondition extends SkillCondition implements ICasterCondition, IEntityCondition {
   @MythicField(
      name = "level",
      aliases = {"l"},
      description = "The level range to match"
   )
   private final RangedDouble data;

   public LevelCondition(String line, MythicLineConfig config) {
      super(line);
      this.data = new RangedDouble(config.getString(new String[]{"level", "l"}, this.conditionVar));
   }

   @Override
   public boolean check(AbstractEntity target) {
      if (getPlugin().getMobManager().isActiveMob(target)) {
         ActiveMob am = getPlugin().getMobManager().getMythicMobInstance(target);
         return this.matches(am.getLevel());
      } else {
         return true;
      }
   }

   @Override
   public boolean check(SkillCaster caster) {
      return this.matches(caster.getLevel());
   }

   private boolean matches(double level) {
      return this.data.getOperation() == RangedDouble.Operation.EQUALS && this.data.getMin() == Math.rint(this.data.getMin())
         ? (int)level == (int)this.data.getMin()
         : this.data.equals(level);
   }
}
