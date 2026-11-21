package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Joikd",
   name = "hasParent",
   aliases = {},
   version = "4.3",
   description = "Tests if the target mob has a parent"
)
public class HasParentCondition extends SkillCondition implements IEntityCondition {
   public HasParentCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity target) {
      ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(target);
      return am != null && am.getParentUUID().isPresent();
   }
}
