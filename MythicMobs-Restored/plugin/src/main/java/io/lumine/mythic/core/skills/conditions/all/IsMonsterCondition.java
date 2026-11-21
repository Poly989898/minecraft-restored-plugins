package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "ismonster",
   aliases = {},
   description = "If the target is a monster"
)
public class IsMonsterCondition extends SkillCondition implements IEntityCondition {
   public IsMonsterCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      return entity.isMonster();
   }
}
