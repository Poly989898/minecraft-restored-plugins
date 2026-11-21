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
   name = "trigger",
   aliases = {},
   description = "Targets the entity that triggered the skill"
)
public class TriggerTargeter extends IEntitySelector {
   public TriggerTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      ArrayList<AbstractEntity> targets = new ArrayList<>(1);
      targets.add(data.getTrigger());
      return targets;
   }
}
