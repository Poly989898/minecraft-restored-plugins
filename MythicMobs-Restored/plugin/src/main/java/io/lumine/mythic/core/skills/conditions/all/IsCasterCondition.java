package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "iscaster",
   aliases = {},
   description = "If the target is the caster of the skill"
)
public class IsCasterCondition extends SkillCondition implements IEntityComparisonCondition {
   public IsCasterCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractEntity target) {
      return entity.getUniqueId().equals(target.getUniqueId());
   }
}
