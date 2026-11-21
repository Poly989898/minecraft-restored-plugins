package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "trackedplayers",
   aliases = {"tracked"},
   description = "Targets players that are within render distance of the caster"
)
public class TrackedPlayersTargeter extends IEntitySelector {
   public TrackedPlayersTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      Collection<AbstractEntity> targets = Lists.newArrayList();
      targets.addAll(this.getPlugin().getVolatileCodeHandler().getEntityHandler().getTrackedPlayers(data.getCaster().getEntity()));
      return targets;
   }
}
