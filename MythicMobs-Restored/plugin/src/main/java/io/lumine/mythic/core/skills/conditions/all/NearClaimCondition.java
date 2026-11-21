package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "nearClaim",
   aliases = {"nearClaims"},
   description = "If the target location is near any supported land protection claims"
)
public class NearClaimCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "radius",
      aliases = {"r"},
      description = "The radius to test",
      defValue = "16"
   )
   private int radius;

   public NearClaimCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.radius = mlc.getInteger(new String[]{"radius", "r"}, 16);
   }

   @Override
   public boolean check(AbstractLocation target) {
      return getPlugin().getCompatibility().isNearClaim(target, this.radius);
   }
}
