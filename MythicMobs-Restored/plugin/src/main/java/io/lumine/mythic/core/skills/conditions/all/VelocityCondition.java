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
   name = "velocity",
   aliases = {},
   version = "5.0",
   description = "Checks the velocity of the target entity against a range."
)
public class VelocityCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "velocity",
      aliases = {"v"},
      description = "The velocity to check for"
   )
   private RangedDouble velocity;

   public VelocityCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.velocity = new RangedDouble(mlc.getString(new String[]{"velocity", "v"}, this.conditionVar));
   }

   @Override
   public boolean check(AbstractEntity e) {
      return this.velocity.equals(e.getBukkitEntity().getVelocity().length());
   }
}
