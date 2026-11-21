package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "randomLocationsNearTargets",
   aliases = {"randomLocationsNearTarget", "randomLocationsNearTargetEntities", "randomLocationsNearTargetLocations", "RLNT", "RLNTE", "RLNTL"},
   description = "Targets random locations near the inherited targets"
)
public class RandomLocationsNearTargetsTargeter extends ILocationSelector {
   private PlaceholderInt amount;
   private PlaceholderInt maxRadius;
   private PlaceholderInt minRadius;
   private PlaceholderInt spacing;
   private boolean onSurface;

   public RandomLocationsNearTargetsTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 5);
      this.maxRadius = mlc.getPlaceholderInteger(new String[]{"radius", "r", "maxradius", "maxr"}, 5);
      this.minRadius = mlc.getPlaceholderInteger(new String[]{"minradius", "minr"}, 0);
      this.spacing = mlc.getPlaceholderInteger(new String[]{"spacing", "s"}, 0);
      this.onSurface = mlc.getBoolean(new String[]{"onsurface", "surface"}, false);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = Lists.newArrayList();
      int amount = Math.max(0, this.amount.get(data));
      int amountSq = amount * amount;
      int rawMaxRadius = this.maxRadius.get(data);
      int rawMinRadius = this.minRadius.get(data);
      int maxRadius = Math.max(0, Math.max(rawMaxRadius, rawMinRadius));
      int minRadius = Math.max(0, Math.min(rawMinRadius, maxRadius));
      int spacing = Math.max(0, this.spacing.get(data));
      double minRadiusSq = (double)minRadius * minRadius;
      double spacingSq = (double)spacing * spacing;
      if (data.getEntityTargets() != null && !data.getEntityTargets().isEmpty()) {
         forEachLoop:
         for (AbstractEntity e : data.getEntityTargets()) {
            int i = 0;
            int j = 0;

            while (true) {
               AbstractLocation l;
               while (true) {
                  if (i >= amount || j >= amountSq) {
                     continue forEachLoop;
                  }

                  j++;
                  this.getPlugin().getMobManager();
                  l = MobExecutor.findSafeSpawnLocation(e.getLocation(), maxRadius, 0.0, 1, false, this.onSurface);
                  if (minRadius <= 0 || !(l.distanceSquared(e.getLocation()) < minRadiusSq)) {
                     if (spacing <= 0) {
                        break;
                     }

                     boolean bad = false;

                     for (AbstractLocation l2 : targets) {
                        if (l.distanceSquared(l2) < spacingSq) {
                           bad = true;
                           break;
                        }
                     }

                     if (!bad) {
                        break;
                     }
                  }
               }

               targets.add(this.mutate(data, l));
               i++;
            }
         }
      } else if (data.getLocationTargets() != null && !data.getLocationTargets().isEmpty()) {
         forEachLoop:
         for (AbstractLocation e : data.getLocationTargets()) {
            int i = 0;
            int j = 0;

            while (true) {
               AbstractLocation l;
               while (true) {
                  if (i >= amount || j >= amountSq) {
                     continue forEachLoop;
                  }

                  j++;
                  this.getPlugin().getMobManager();
                  l = MobExecutor.findSafeSpawnLocation(e, maxRadius, 0.0, 1, false, this.onSurface);
                  if (minRadius <= 0 || !(l.distanceSquared(e) < minRadiusSq)) {
                     if (spacing <= 0) {
                        break;
                     }

                     boolean bad = false;

                     for (AbstractLocation l2x : targets) {
                        if (l.distanceSquared(l2x) < spacingSq) {
                           bad = true;
                           break;
                        }
                     }

                     if (!bad) {
                        break;
                     }
                  }
               }

               targets.add(this.mutate(data, l));
               i++;
            }
         }
      }

      return targets;
   }
}
