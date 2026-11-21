package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "motionz",
   aliases = {"motz"},
   version = "5.0",
   description = "Checks the Z motion of the target entity against a range."
)
public class MotionZCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "velocity",
      aliases = {"v"},
      description = "The velocity to check for"
   )
   private final RangedDouble velocity;

   public MotionZCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.velocity = new RangedDouble(mlc.getString(new String[]{"velocity", "v"}, this.conditionVar));
   }

   @Override
   public boolean check(AbstractEntity entity) {
      AbstractVector v = getPlugin().getVolatileCodeHandler().getEntityHandler().getEntityMotion(entity);
      double motion = Math.abs(v.getZ());
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking entity Z Motion = {0}", motion);
      return this.velocity.equals(motion);
   }
}
