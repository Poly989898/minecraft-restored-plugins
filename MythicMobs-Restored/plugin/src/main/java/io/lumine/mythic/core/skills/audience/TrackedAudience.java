package io.lumine.mythic.core.skills.audience;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.utils.annotations.MythicAudience;
import java.util.Collection;

@MythicAudience(
   name = "tracked",
   aliases = {"trackedplayers"},
   version = "5.4",
   description = "Players who's clients can render the caster"
)
public class TrackedAudience extends SkillAudience {
   public TrackedAudience(MythicLineConfig mlc) {
      super(mlc);
   }

   @Override
   public Collection<AbstractPlayer> get(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> targets = Lists.newArrayList();
      targets.addAll(getPlugin().getVolatileCodeHandler().getEntityHandler().getTrackedPlayers(data.getCaster().getEntity()));
      return targets;
   }
}
