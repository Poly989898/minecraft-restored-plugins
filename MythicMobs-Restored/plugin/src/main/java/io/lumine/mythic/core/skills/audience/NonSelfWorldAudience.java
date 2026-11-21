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
   name = "nonSelfWorld",
   aliases = {"nonSelf"},
   version = "4.11",
   description = "All players in the world other than the caster"
)
public class NonSelfWorldAudience extends SkillAudience {
   public NonSelfWorldAudience(MythicLineConfig mlc) {
      super(mlc);
   }

   @Override
   public Collection<AbstractPlayer> get(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> targets = Lists.newArrayList();
      targets.addAll(data.getCaster().getEntity().getWorld().getPlayers());
      targets.remove(data.getCaster().getEntity());
      return targets;
   }
}
