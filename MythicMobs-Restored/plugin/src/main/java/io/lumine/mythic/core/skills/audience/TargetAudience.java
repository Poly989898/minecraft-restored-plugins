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
   name = "target",
   aliases = {},
   version = "4.11",
   description = "The target of the skill"
)
public class TargetAudience extends SkillAudience {
   public TargetAudience(MythicLineConfig mlc) {
      super(mlc);
   }

   @Override
   public Collection<AbstractPlayer> get(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> ret = Lists.newArrayList();
      if (target.isPlayer()) {
         ret.add(target.asPlayer());
      }

      return ret;
   }
}
