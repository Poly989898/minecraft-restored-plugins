package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "trackedLocation",
   aliases = {},
   description = "Targets the caster's tracked location"
)
public class TrackedLocationTargeter extends ILocationSelector {
   public TrackedLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = Lists.newArrayList();
      if (data.getCaster() instanceof ActiveMob am) {
         if (am.getTrackedLocation() != null) {
            targets.add(this.mutate(data, am.getTrackedLocation()));
         }

         return targets;
      } else {
         return targets;
      }
   }
}
