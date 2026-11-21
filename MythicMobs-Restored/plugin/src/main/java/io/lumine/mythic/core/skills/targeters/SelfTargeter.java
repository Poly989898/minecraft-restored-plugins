package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.ArrayList;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "self",
   aliases = {"caster", "boss", "mob"},
   description = "Targets the caster"
)
public class SelfTargeter extends IEntitySelector {
   public SelfTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.targetSelf = true;
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      ArrayList<AbstractEntity> targets = new ArrayList<>(1);
      targets.add(data.getCaster().getEntity());
      return targets;
   }
}
