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
   name = "caster",
   aliases = {"self"},
   version = "4.11.1",
   description = "The caster of the skill."
)
public class CasterAudience extends SkillAudience {
   public CasterAudience(MythicLineConfig mlc) {
      super(mlc);
   }

   @Override
   public Collection<AbstractPlayer> get(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> targets = Lists.newArrayList();
      if (data.getCaster().getEntity().isPlayer()) {
         targets.add(data.getCaster().getEntity().asPlayer());
      }

      return targets;
   }
}
