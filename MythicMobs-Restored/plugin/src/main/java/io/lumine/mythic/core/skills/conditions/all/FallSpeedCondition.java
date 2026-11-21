package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "fallSpeed",
   aliases = {"fallingspeed"},
   description = "If the fall speed of the target is within the given range"
)
public class FallSpeedCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "speed",
      aliases = {"s"},
      description = "The velocity to match"
   )
   private final RangedDouble speed;

   public FallSpeedCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.speed = new RangedDouble(mlc.getString(new String[]{"speed", "s"}, this.conditionVar));
   }

   @Override
   public boolean check(AbstractEntity e) {
      double magnitude = e.getBukkitEntity().getVelocity().length() * 10.0;
      return this.speed.equals(magnitude);
   }
}
