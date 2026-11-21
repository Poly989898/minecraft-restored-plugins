package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin/jaylawl",
   name = "threat",
   aliases = {"threatchange", "threatmod"},
   description = "Modifies the target entities' threat level"
)
public class ThreatMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderString mode;
   protected PlaceholderDouble amount;

   public ThreatMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.target_creative = false;
      this.mode = mlc.getPlaceholderString(new String[]{"mode", "m"}, "add");
      this.amount = mlc.getPlaceholderDouble(new String[]{"amount", "a"}, 1.0);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!(data.getCaster() instanceof ActiveMob)) {
         return SkillResult.INVALID_TARGET;
      } else {
         ActiveMob am = (ActiveMob)data.getCaster();
         if (target.isDead() || target.getHealth() <= 0.0) {
            return SkillResult.INVALID_TARGET;
         } else if (am.getThreatTable() == null) {
            return SkillResult.INVALID_CONFIG;
         } else {
            if (MythicBukkit.inst().getMobManager().isActiveMob(target)) {
               ActiveMob amt = MythicBukkit.inst().getMobManager().getMythicMobInstance(target);
               if (am.hasFaction() && am.getFaction().equals(amt.getFaction())) {
                  return SkillResult.INVALID_TARGET;
               }
            }

            String mode = this.mode.get(data, target);
            double amount = this.amount.get(data, target);
            return this.modifyThreat(am.getThreatTable(), target, mode, amount);
         }
      }
   }

   protected SkillResult modifyThreat(ActiveMob.ThreatTable threatTable, AbstractEntity targetEntity, String mode, double amount) {
      String toLowerCase = mode.toLowerCase();
      switch (toLowerCase) {
         case "add":
            if (amount > 0.0) {
               threatTable.threatGain(targetEntity, amount);
            } else {
               threatTable.threatLoss(targetEntity, amount * -1.0);
            }
            break;
         case "remove":
            if (amount > 0.0) {
               threatTable.threatLoss(targetEntity, amount);
            } else {
               threatTable.threatGain(targetEntity, amount * -1.0);
            }
            break;
         case "multiply":
            threatTable.threatSet(targetEntity, threatTable.getThreat(targetEntity) * amount);
            break;
         case "divide":
            if (amount == 0.0) {
               return SkillResult.CONDITION_FAILED;
            }

            threatTable.threatSet(targetEntity, threatTable.getThreat(targetEntity) / amount);
            break;
         case "set":
            threatTable.threatSet(targetEntity, amount);
            break;
         case "reset":
         case "delete":
            threatTable.threatLoss(targetEntity, threatTable.getThreat(targetEntity));
            break;
         case "taunt":
         case "forcetop":
         case "force":
         case "topthreat":
         case "top":
            threatTable.Taunt(targetEntity);
            break;
         default:
            return SkillResult.INVALID_CONFIG;
      }

      return SkillResult.SUCCESS;
   }
}
