package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Seyarada",
   name = "highestblock",
   aliases = {},
   description = "Targets the highest block at the skill origin"
)
public class HighestBlockTargeter extends ILocationSelector {
   public HighestBlockTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      HashSet<AbstractLocation> targets = new HashSet<>();
      AbstractLocation highestBlock = data.getOrigin().getWorld().getHighestBlock(data.getOrigin());
      targets.add(this.mutate(data, highestBlock.clone()));
      return targets;
   }
}
