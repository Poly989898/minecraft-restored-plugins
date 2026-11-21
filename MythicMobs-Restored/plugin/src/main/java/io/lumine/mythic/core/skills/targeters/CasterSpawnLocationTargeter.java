package io.lumine.mythic.core.skills.targeters;

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
   name = "casterSpawnLocation",
   aliases = {"casterSpawn"},
   description = "Targets the caster's spawn location"
)
public class CasterSpawnLocationTargeter extends ILocationSelector {
   public CasterSpawnLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      HashSet<AbstractLocation> targets = new HashSet<>();
      if (data.getCaster() instanceof ActiveMob am) {
         targets.add(this.mutate(data, am.getSpawnLocation().clone()));
         return targets;
      } else {
         return targets;
      }
   }
}
