package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "ischild",
   aliases = {"child"},
   description = "If the target is a child of the caster"
)
public class IsChildCondition extends SkillCondition implements ISkillMetaComparisonCondition {
   public IsChildCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      for (AbstractEntity entity : meta.getCaster().getChildren()) {
         if (entity.equals(target)) {
            return true;
         }
      }

      return false;
   }
}
