package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setTarget",
   aliases = {"target"},
   description = "Force the caster to attack the target entity"
)
public class SetTargetMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public SetTargetMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.target_creative = false;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!(data.getCaster() instanceof ActiveMob)) {
         return SkillResult.INVALID_TARGET;
      } else {
         ActiveMob am = (ActiveMob)data.getCaster();
         if (!target.isDead() && !(target.getHealth() <= 0.0)) {
            if (this.getPlugin().getMobManager().isActiveMob(target)) {
               ActiveMob amt = this.getPlugin().getMobManager().getMythicMobInstance(target);
               if (am.hasFaction() && am.getFaction().equals(amt.getFaction())) {
                  return SkillResult.INVALID_TARGET;
               }
            }

            if (am.getThreatTable() == null) {
               am.setTarget(target);
            } else {
               am.getThreatTable().Taunt(target);
            }

            return SkillResult.SUCCESS;
         } else {
            return SkillResult.INVALID_TARGET;
         }
      }
   }
}
