package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "playerNotWithin",
   aliases = {"playersnotwithin"},
   description = "Checks if any players are within a radius of the target"
)
public class PlayerNotWithinCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "distance",
      aliases = {"d"},
      description = "The radius to check in"
   )
   private double distance;

   public PlayerNotWithinCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.distance = Math.pow(Double.valueOf(mlc.getString(new String[]{"distance", "d"}, "0", this.conditionVar)), 2.0);
   }

   @Override
   public boolean check(AbstractLocation location) {
      for (AbstractPlayer p : location.getWorld().getPlayers()) {
         if (p.getWorld().equals(location.getWorld()) && location.distanceSquared(p.getLocation()) <= this.distance) {
            return false;
         }
      }

      return true;
   }
}
