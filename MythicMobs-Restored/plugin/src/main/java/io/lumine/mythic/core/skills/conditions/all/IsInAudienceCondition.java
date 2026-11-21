package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import java.util.Set;
import java.util.UUID;

@MythicCondition(
   author = "evlad",
   name = "isInAudience",
   aliases = {"inaudience", "audiencecontains"},
   description = "Checks if the target entity is in the execution audience"
)
public class IsInAudienceCondition extends SkillCondition implements ISkillMetaCondition, ISkillMetaComparisonCondition {
   public IsInAudienceCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      return this.checkAudience(meta, meta.getCaster().getEntity());
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      return this.checkAudience(meta, target);
   }

   private boolean checkAudience(SkillMetadata meta, AbstractEntity entity) {
      if (!entity.isPlayer()) {
         return false;
      } else if (!meta.hasExecutionAudience()) {
         return true;
      } else {
         Set<UUID> audience = meta.getExecutionAudience().orElse(null);
         return audience != null && !audience.isEmpty() ? audience.contains(entity.getUniqueId()) : false;
      }
   }
}
