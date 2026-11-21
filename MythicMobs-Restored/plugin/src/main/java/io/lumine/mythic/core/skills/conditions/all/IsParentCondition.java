package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "parent",
   aliases = {"isParent"},
   description = "Checks if the target entity is the parent/summoner of the caster"
)
public class IsParentCondition extends SkillCondition implements IEntityComparisonCondition {
   public IsParentCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractEntity target) {
      if (getPlugin().getMobManager().isActiveMob(entity)) {
         ActiveMob am = getPlugin().getMobManager().getMythicMobInstance(entity);
         if (am.getParentUUID().isPresent()) {
            return am.getParentUUID().get().equals(target.getUniqueId());
         }
      }

      return false;
   }
}
