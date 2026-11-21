package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "taunt",
   description = "Modifies your threat level with the target entity"
)
public class TauntMechanic extends ThreatMechanic {
   public TauntMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.mode = mlc.getPlaceholderString(new String[]{"mode", "m"}, "taunt");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      ActiveMob targetMob = this.getPlugin().getMobManager().getMythicMobInstance(target);
      if (targetMob == null) {
         return SkillResult.INVALID_TARGET;
      } else if (target.isDead() || target.getHealth() <= 0.0) {
         return SkillResult.INVALID_TARGET;
      } else if (targetMob.getThreatTable() == null) {
         return SkillResult.INVALID_CONFIG;
      } else {
         SkillCaster caster = data.getCaster();
         AbstractEntity casterEntity = caster.getEntity();
         String casterFaction;
         if (caster instanceof ActiveMob am) {
            casterFaction = am.getFaction();
         } else if (casterEntity.isPlayer()) {
            casterFaction = this.getPlugin().getPlayerManager().getFactionProvider().getFaction(casterEntity.asPlayer()).orElse(null);
         } else {
            casterFaction = null;
         }

         if (casterFaction != null && casterFaction.equals(targetMob.getFaction())) {
            return SkillResult.INVALID_TARGET;
         } else {
            String mode = this.mode.get(data, target);
            double amount = this.amount.get(data, target);
            return this.modifyThreat(targetMob.getThreatTable(), casterEntity, mode, amount);
         }
      }
   }
}
