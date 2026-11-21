package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Joshinn",
   name = "premium",
   aliases = {"ispremium", "iscool"},
   description = "Whether or not premium is enabledd."
)
public class PremiumCondition extends SkillCondition implements ICasterCondition {
   public PremiumCondition(String line, MythicLineConfig config) {
      super(line);
   }

   @Override
   public boolean check(SkillCaster caster) {
      return MythicBukkit.isVolatile();
   }
}
