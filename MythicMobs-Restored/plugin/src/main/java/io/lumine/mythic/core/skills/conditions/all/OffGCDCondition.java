package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "offGCD",
   aliases = {},
   description = "Checks if the target mob has an active Global Cooldown"
)
public class OffGCDCondition extends SkillCondition implements ICasterCondition, IEntityCondition {
   public OffGCDCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (MythicBukkit.inst().getMobManager().isActiveMob(entity)) {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
         return am.getGlobalCooldown() <= 0;
      } else {
         return false;
      }
   }

   @Override
   public boolean check(SkillCaster caster) {
      return caster.getGlobalCooldown() <= 0;
   }
}
