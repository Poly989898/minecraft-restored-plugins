package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "spawnLocation",
   aliases = {},
   description = "Targets the caster's spawn location"
)
public class SpawnLocationTargeter extends ILocationSelector {
   public SpawnLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      HashSet<AbstractLocation> targets = Sets.newHashSet();
      if (data.getCaster() instanceof ActiveMob am) {
         targets.add(this.mutate(data, am.getSpawnLocation()));
         return targets;
      } else {
         return targets;
      }
   }
}
