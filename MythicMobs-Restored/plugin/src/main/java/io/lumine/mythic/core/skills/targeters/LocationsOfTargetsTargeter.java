package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "locationsOfTargets",
   aliases = {"locationOfTarget", "LOT"},
   description = "Targets the location of the inherited targets"
)
public class LocationsOfTargetsTargeter extends ILocationSelector {
   public LocationsOfTargetsTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      HashSet<AbstractLocation> targets = new HashSet<>();
      if (data.getEntityTargets() != null) {
         for (AbstractEntity e : data.getEntityTargets()) {
            targets.add(this.mutate(data, e.getLocation()));
         }
      } else if (data.getLocationTargets() != null) {
         for (AbstractLocation e : data.getLocationTargets()) {
            targets.add(this.mutate(data, e));
         }
      }

      return targets;
   }
}
