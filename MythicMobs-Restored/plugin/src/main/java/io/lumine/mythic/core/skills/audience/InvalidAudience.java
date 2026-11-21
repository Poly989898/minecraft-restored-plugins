package io.lumine.mythic.core.skills.audience;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillAudience;
import java.util.Collection;

public class InvalidAudience extends SkillAudience {
   public InvalidAudience(MythicLineConfig mlc) {
      super(mlc);
   }

   @Override
   public Collection<AbstractPlayer> get(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> targets = Lists.newArrayList();
      if (target.isPlayer()) {
         targets.add(target.asPlayer());
      }

      return targets;
   }
}
