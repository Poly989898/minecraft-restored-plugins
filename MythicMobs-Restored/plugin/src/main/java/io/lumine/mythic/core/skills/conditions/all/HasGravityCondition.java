package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "jaylawl",
   name = "hasgravity",
   aliases = {"gravity"},
   description = "Tests if the target has gravity"
)
public class HasGravityCondition extends SkillCondition implements IEntityCondition {
   public HasGravityCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity target) {
      return target.hasGravity();
   }
}
