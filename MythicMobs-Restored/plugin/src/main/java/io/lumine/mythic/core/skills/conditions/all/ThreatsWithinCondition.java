package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedInt;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "threatWithin",
   description = "Checks if any players are within a radius of the target"
)
public class ThreatsWithinCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "distance",
      aliases = {"d"},
      description = "The radius to check in"
   )
   private double distance;
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount to check"
   )
   private RangedInt amount;

   public ThreatsWithinCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.distance = Math.pow(Double.valueOf(mlc.getString(new String[]{"distance", "d"}, this.conditionVar)), 2.0);
      String a = mlc.getString(new String[]{"amount", "a"}, "0", this.conditionVar);
      this.amount = new RangedInt(a);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      if (meta.getCaster() instanceof ActiveMob am && am.hasThreatTable()) {
         int count = 0;

         for (AbstractEntity entity : am.getThreatTable().getAllThreatTargets()) {
            if ((!entity.isPlayer() || !entity.asPlayer().isInSpectatorMode() && !entity.asPlayer().isInCreativeMode())
               && entity.getLocation().distanceSquared(am.getLocation()) <= this.distance) {
               count++;
            }
         }

         return this.amount.equals(count);
      } else {
         return false;
      }
   }
}
