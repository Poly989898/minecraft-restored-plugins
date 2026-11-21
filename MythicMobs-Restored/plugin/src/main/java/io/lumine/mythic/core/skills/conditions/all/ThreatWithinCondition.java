package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "threatWithin",
   description = "Checks if any players are within a radius of the target"
)
public class ThreatWithinCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "distance",
      aliases = {"d"},
      description = "The radius to check in"
   )
   private double distance;

   public ThreatWithinCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.distance = Math.pow(Double.valueOf(mlc.getString(new String[]{"distance", "d"}, "0", this.conditionVar)), 2.0);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      if (meta.getCaster() instanceof ActiveMob am && am.hasThreatTable()) {
         for (AbstractEntity entity : am.getThreatTable().getAllThreatTargets()) {
            if ((!entity.isPlayer() || !entity.asPlayer().isInSpectatorMode() && !entity.asPlayer().isInCreativeMode())
               && entity.getLocation().distanceSquared(am.getLocation()) <= this.distance) {
               return true;
            }
         }
      }

      return false;
   }
}
