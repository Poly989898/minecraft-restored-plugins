package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Phil",
   name = "randomLocationsNearCaster",
   aliases = {"RLNC", "randomLocations"},
   description = "Targets random locations near the caster"
)
public class RandomLocationsTargeter extends ILocationSelector {
   private PlaceholderInt amount;
   private PlaceholderInt maxRadius;
   private PlaceholderInt minRadius;
   private PlaceholderInt spacing;
   private boolean onSurface;

   public RandomLocationsTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 5);
      this.maxRadius = mlc.getPlaceholderInteger(new String[]{"radius", "r", "maxradius", "maxr"}, 5);
      this.minRadius = mlc.getPlaceholderInteger(new String[]{"minradius", "minr"}, 0);
      this.spacing = mlc.getPlaceholderInteger(new String[]{"spacing", "s"}, 0);
      this.onSurface = mlc.getBoolean(new String[]{"onsurface", "onsurf", "os"}, false);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      HashSet<AbstractLocation> targets = new HashSet<>();
      int amount = Math.max(0, this.amount.get(data));
      int amountSq = amount * amount;
      AbstractLocation loc = data.getCaster().getLocation();
      int rawMaxRadius = this.maxRadius.get(data);
      int rawMinRadius = this.minRadius.get(data);
      int maxRadius = Math.max(0, Math.max(rawMaxRadius, rawMinRadius));
      int minRadius = Math.max(0, Math.min(rawMinRadius, maxRadius));
      int spacing = Math.max(0, this.spacing.get(data));
      double minRadiusSq = (double)minRadius * minRadius;
      double spacingSq = (double)spacing * spacing;
      int i = 0;
      int j = 0;

      while (i < amount && j < amountSq) {
         j++;
         MythicBukkit.inst().getMobManager();
         AbstractLocation l = MobExecutor.findSafeSpawnLocation(loc, maxRadius, 1.0, 1, true, this.onSurface);
         if (minRadius <= 0 || !(l.distanceSquared(loc) < minRadiusSq)) {
            if (spacing > 0) {
               boolean bad = false;

               for (AbstractLocation l2 : targets) {
                  if (l.distanceSquared(l2) < spacingSq) {
                     bad = true;
                     break;
                  }
               }

               if (bad) {
                  continue;
               }
            }

            targets.add(this.mutate(data, l));
            i++;
         }
      }

      return targets;
   }
}
