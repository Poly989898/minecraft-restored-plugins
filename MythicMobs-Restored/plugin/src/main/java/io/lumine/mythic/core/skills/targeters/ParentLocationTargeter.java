package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.utils.serialize.Optl;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "parentlocation",
   aliases = {"summonerlocation"},
   description = "Targets the caster's parent/summoner's location"
)
public class ParentLocationTargeter extends ILocationSelector {
   public ParentLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = Lists.newArrayList();
      if (data.getCaster() instanceof ActiveMob am) {
         Optl<AbstractEntity> maybeEntity = am.getParent();
         if (maybeEntity.isPresent()) {
            targets.add(this.mutate(data, maybeEntity.get().getLocation()));
         }
      }

      return targets;
   }
}
