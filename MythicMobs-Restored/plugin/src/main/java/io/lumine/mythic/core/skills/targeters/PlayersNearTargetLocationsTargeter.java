package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "playersNearTargetLocations",
   aliases = {"playersNearTargetLocation", "PNTL"},
   description = "Targets all players near the inherited locations"
)
public class PlayersNearTargetLocationsTargeter extends IEntitySelector {
   double radius;

   public PlayersNearTargetLocationsTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getDouble(new String[]{"radius", "r"}, 5.0);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      Collection<AbstractEntity> targets = Lists.newArrayList();
      Collection<AbstractLocation> baseLocations = this.getMetaLocations(data);
      double range = Math.pow(this.radius, 2.0);

      for (AbstractEntity p : data.getCaster().getEntity().getWorld().getPlayers()) {
         if (data.getCaster().getLocation().getWorld().equals(p.getWorld()) && !p.getUniqueId().equals(data.getCaster().getEntity().getUniqueId())) {
            for (AbstractLocation l : baseLocations) {
               if (this.distanceSquared(l, p) < range) {
                  targets.add(p);
                  break;
               }
            }
         }
      }

      return targets;
   }
}
