package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.damage.DamagingMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "baseDamage",
   aliases = {"bd", "weaponDamage", "wd"},
   description = "Deals a percent of the mob's damage stat as damage"
)
public class DamageBaseMechanic extends DamagingMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "multiplier",
      aliases = {"m"},
      description = "The percentage of damage to deal",
      defValue = "1"
   )
   protected PlaceholderDouble multiplier;
   protected boolean useAttribute;

   public DamageBaseMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.multiplier = mlc.getPlaceholderDouble(new String[]{"multiplier", "m"}, 1.0);
      this.useAttribute = mlc.getBoolean(new String[]{"useAttribute", "attribute", "attr"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isDead() && !(target.getHealth() <= 0.0)) {
         SkillCaster caster = data.getCaster();
         double multiplier = this.multiplier.get(data, target);
         if (this.useAttribute) {
            double damage = data.getCaster().getEntity().getDamage();
            damage *= this.powerAffectsDamage ? multiplier * data.getPower() : multiplier;
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ DamageBaseMechanic fired for {0} with {1} power", damage, data.getPower());
            this.doDamage(data, target, damage);
            return SkillResult.SUCCESS;
         } else {
            if (caster instanceof ActiveMob am) {
               if (am.isUsingDamageSkill()) {
                  return SkillResult.INVALID_TARGET;
               }

               double damage;
               if (this.powerAffectsDamage) {
                  damage = am.getDamage() * multiplier * data.getPower();
               } else {
                  damage = am.getDamage() * multiplier;
               }

               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ DamageBaseMechanic fired for {0} with {1} power", damage, data.getPower());
               this.doDamage(data, target, damage);
            } else {
               double damage;
               if (this.powerAffectsDamage) {
                  damage = multiplier * data.getPower();
               } else {
                  damage = multiplier;
               }

               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ DamageBaseMechanic fired for {0} with {1} power", damage, data.getPower());
               this.doDamage(data, target, damage);
            }

            return SkillResult.SUCCESS;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
