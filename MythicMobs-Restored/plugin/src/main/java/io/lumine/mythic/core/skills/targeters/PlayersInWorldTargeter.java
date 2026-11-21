package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.List;

@MythicTargeter(
   author = "Ashijin",
   name = "PlayersInWorld",
   aliases = {"WORLD"},
   description = "Targets all players in the caster's world"
)
public class PlayersInWorldTargeter extends IEntitySelector {
   public PlayersInWorldTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      List<AbstractEntity> targets = Lists.newArrayList();
      targets.addAll(data.getCaster().getEntity().getWorld().getPlayers());
      return targets;
   }
}
