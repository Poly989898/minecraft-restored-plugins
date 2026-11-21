package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "vehicleisdead",
   aliases = {},
   description = "If the target's vehicle is dead"
)
public class VehicleIsDeadCondition extends SkillCondition implements IEntityComparisonCondition {
   public VehicleIsDeadCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractEntity target) {
      return target.getVehicle() != null && (target.getVehicle().isDead() || !target.getVehicle().isValid());
   }
}
