package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "line",
   aliases = {},
   description = "Targets points in a line from the caster to the target location"
)
public class LineTargeter extends ILocationSelector {
   protected float radius = 1.0F;
   protected float radiusSq;
   protected boolean fromOrigin = false;

   public LineTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getFloat(new String[]{"radius", "r"}, this.radius);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.radiusSq = this.radius * this.radius;
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      HashSet<AbstractLocation> targets = new HashSet<>();
      AbstractLocation sl;
      if (this.fromOrigin) {
         sl = data.getOrigin().clone();
      } else {
         sl = data.getCaster().getLocation();
      }

      double range = 1.0;
      if (data.getLocationTargets() != null && data.getLocationTargets().size() > 0) {
         for (AbstractLocation l : data.getLocationTargets()) {
            double d = l.distanceSquared(sl);
            if (d > range) {
               range = d;
            }
         }
      } else {
         if (data.getEntityTargets() == null || data.getEntityTargets().size() <= 0) {
            return targets;
         }

         for (AbstractEntity lx : data.getEntityTargets()) {
            double d = lx.getLocation().distanceSquared(sl);
            if (d > range) {
               range = d;
            }
         }
      }

      if (data.getLocationTargets() != null && data.getLocationTargets().size() > 0) {
         data.getLocationTargets().forEach(lxx -> {
            int c = (int)Math.ceil(sl.distance(lxx) / this.radius) - 1;
            if (c > 0) {
               AbstractVector v = lxx.toVector().subtract(sl.toVector()).normalize().multiply(this.radius);
               AbstractLocation l2 = sl.clone();

               for (int i = 0; i < c; i++) {
                  l2.add(v);
                  targets.add(l2.clone());
               }
            }
         });
      } else if (data.getEntityTargets() != null && data.getEntityTargets().size() > 0) {
         data.getEntityTargets().forEach(ee -> {
            AbstractLocation lxx = ee.getLocation();
            int c = (int)Math.ceil(sl.distance(lxx) / this.radius) - 1;
            if (c > 0) {
               AbstractVector v = lxx.toVector().subtract(sl.toVector()).normalize().multiply(this.radius);
               AbstractLocation l2 = sl.clone();

               for (int i = 0; i < c; i++) {
                  l2.add(v);
                  targets.add(l2.clone());
               }
            }
         });
      }

      return targets;
   }
}
