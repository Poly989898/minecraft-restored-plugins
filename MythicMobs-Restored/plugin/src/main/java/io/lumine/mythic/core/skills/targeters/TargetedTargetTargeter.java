package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "targetedTarget",
   aliases = {"targeted"},
   description = "Targets the inherited targets"
)
public class TargetedTargetTargeter extends IEntitySelector {
   public TargetedTargetTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      HashSet<AbstractEntity> targets = Sets.newHashSet();
      if (data.getEntityTargets() != null) {
         targets.addAll(data.getEntityTargets());
      }

      return targets;
   }
}
