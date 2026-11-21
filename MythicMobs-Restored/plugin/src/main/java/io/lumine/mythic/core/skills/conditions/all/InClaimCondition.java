package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "inClaim",
   aliases = {"inclaim"},
   description = "If the target location is inside a supported Land Protection claim"
)
public class InClaimCondition extends SkillCondition implements ILocationCondition {
   public InClaimCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractLocation target) {
      return getPlugin().getCompatibility().isLocationClaimed(target);
   }
}
