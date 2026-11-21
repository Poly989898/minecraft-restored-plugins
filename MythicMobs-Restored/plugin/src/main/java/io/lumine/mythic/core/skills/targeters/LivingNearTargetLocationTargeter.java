package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.LinkedHashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "livingNearTargetLocation",
   aliases = {"LivingEntitiesNearTargetLocation", "LNTL", "ENTL", "ENT"},
   description = "Targets entities near the inherited target location"
)
public class LivingNearTargetLocationTargeter extends IEntitySelector {
   double radius;

   public LivingNearTargetLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getDouble(new String[]{"radius", "r"}, 5.0);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      Collection<AbstractEntity> targets = new LinkedHashSet<>();
      AbstractWorld world = data.getCaster().getEntity().getWorld();
      double range = this.radius * this.radius;

      for (AbstractLocation l : data.getLocationTargets()) {
         if (!world.equals(l.getWorld())) {
            throw new IllegalArgumentException("Cannot measure distance between two different worlds.");
         }

         for (AbstractEntity p : world.getEntitiesNearLocation(l, this.radius, e -> e.isLiving() && e.isValid())) {
            if (!p.getUniqueId().equals(data.getCaster().getEntity().getUniqueId()) && this.distanceSquared(l, p) < range) {
               targets.add(p);
            }
         }
      }

      return targets;
   }
}
