package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "livingInLine",
   aliases = {"entitiesInLine", "livingEntitiesInLine", "LEIL", "EIL"},
   description = "Targets random points in a cone shape"
)
public class LivingInLineTargeter extends IEntitySelector {
   protected PlaceholderDouble radius;
   protected boolean fromOrigin = false;

   public LivingInLineTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, 1.0);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      HashSet<AbstractEntity> targets = new HashSet<>();
      double radius = this.radius.get(data);
      double radiusSq = radius * radius;
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

      SkillCaster am = data.getCaster();
      HashSet<AbstractEntity> possible = new HashSet<>();

      for (AbstractEntity p : am.getEntity().getWorld().getLivingEntities()) {
         if (am.getLocation().getWorld().equals(p.getWorld()) && !p.getUniqueId().equals(am.getEntity().getUniqueId()) && this.distanceSquared(sl, p) <= range) {
            possible.add(p);
         }
      }

      if (data.getLocationTargets() != null && data.getLocationTargets().size() > 0) {
         data.getLocationTargets().forEach(lxx -> {
            int c = (int)Math.ceil(sl.distance(lxx) / radius);
            if (c > 0) {
               AbstractVector v = lxx.toVector().subtract(sl.toVector()).normalize().multiply(radius);
               AbstractLocation l2 = sl.clone();

               for (int i = 0; i < c; i++) {
                  l2.add(v);

                  for (AbstractEntity e : possible) {
                     if (this.distanceSquared(l2, e) <= radiusSq) {
                        targets.add(e);
                     }
                  }
               }
            }
         });
      } else if (data.getEntityTargets() != null && data.getEntityTargets().size() > 0) {
         data.getEntityTargets().forEach(ee -> {
            AbstractLocation lxx = ee.getLocation();
            int c = (int)Math.ceil(sl.distance(lxx) / radius);
            if (c > 0) {
               AbstractVector v = lxx.toVector().subtract(sl.toVector()).normalize().multiply(radius);
               AbstractLocation l2 = sl.clone();

               for (int i = 0; i < c; i++) {
                  l2.add(v);

                  for (AbstractEntity e : possible) {
                     if (this.distanceSquared(l2, e) <= radiusSq) {
                        targets.add(e);
                     }
                  }
               }
            }
         });
      }

      return targets;
   }
}
