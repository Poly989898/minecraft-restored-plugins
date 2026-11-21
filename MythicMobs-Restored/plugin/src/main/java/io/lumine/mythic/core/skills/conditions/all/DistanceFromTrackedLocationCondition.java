package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "distanceFromTrackedLocation",
   aliases = {"distanceFromTL"},
   description = "Tests if the caster is within a certain distance of its tracked location."
)
public class DistanceFromTrackedLocationCondition extends SkillCondition implements ICasterCondition {
   @MythicField(
      name = "distance",
      aliases = {"d"},
      description = "The distance to match"
   )
   protected RangedDouble distance;

   public DistanceFromTrackedLocationCondition(String line, MythicLineConfig mlc) {
      super(line);
      String d = mlc.getString(new String[]{"distance", "d"}, this.conditionVar);
      this.distance = new RangedDouble(d, true);
   }

   @Override
   public boolean check(SkillCaster caster) {
      if (caster instanceof ActiveMob am && am.getTrackedLocation() != null) {
         double diffSq = (float)am.getTrackedLocation().distanceSquared(am.getLocation());
         return this.distance.equals(diffSq);
      } else {
         return false;
      }
   }
}
