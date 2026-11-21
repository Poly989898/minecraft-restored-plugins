package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Sets;
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
   name = "targetedLocation",
   aliases = {"targetedLocations", "targetedLoc"},
   description = "Targets the location the caster is targeting"
)
public class TargetedLocationTargeter extends ILocationSelector {
   public TargetedLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      HashSet<AbstractLocation> targets = Sets.newHashSet();
      if (data.getLocationTargets() != null) {
         for (AbstractLocation location : data.getLocationTargets()) {
            targets.add(this.mutate(data, location));
         }
      } else if (data.getEntityTargets() != null) {
         for (AbstractEntity entity : data.getEntityTargets()) {
            targets.add(this.mutate(data, entity.getLocation()));
         }
      }

      return targets;
   }
}
