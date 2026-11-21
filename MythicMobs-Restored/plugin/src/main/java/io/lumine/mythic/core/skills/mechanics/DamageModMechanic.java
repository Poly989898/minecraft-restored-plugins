package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.damage.DamageModifyingMechanic;
import io.lumine.mythic.core.skills.stats.StatModifierType;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "modifyDamage",
   aliases = {"modDamage"},
   description = "Modifies the current trigger's damage"
)
public class DamageModMechanic extends DamageModifyingMechanic implements INoTargetSkill {
   private PlaceholderDouble amount;
   private StatModifierType modifier;
   private PlaceholderString damageType;
   private boolean modAllDamage;

   public DamageModMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.setThreadSafetyLevel(ThreadSafetyLevel.SYNC_ONLY);
      this.amount = mlc.getPlaceholderDouble(new String[]{"amount", "a"}, 1.0);
      this.damageType = mlc.getPlaceholderString(new String[]{"damagetype", "type", "dt", "t"}, "ALL");
      if (this.damageType != null && (!this.damageType.isStatic() || !this.damageType.get().equalsIgnoreCase("PHYSICAL"))) {
         if (this.damageType.isStatic() && this.damageType.get().equalsIgnoreCase("ALL")) {
            this.modAllDamage = true;
            this.damageType = null;
         }
      } else {
         this.damageType = null;
      }

      String modifierType = mlc.getString(new String[]{"action", "mod", "m"}, StatModifierType.ADDITIVE.toString());
      if (modifierType == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "Modifier type is required");
      } else {
         try {
            this.modifier = StatModifierType.get(modifierType.toUpperCase());
         } catch (Error | Exception e) {
            MythicLogger.errorMechanicConfig(this, mlc, "Invalid modifier type specified!");
         }
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (data.isAsync()) {
         return SkillResult.INVALID_CONFIG;
      } else if (data.getTriggerMetadata() instanceof EntityAttackMetadata entityAttackMetadata) {
         this.doDamageModification(data, entityAttackMetadata);
         entityAttackMetadata.applyToSkillMetadata(data);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_CONFIG;
      }
   }

   private void doDamageModification(SkillMetadata data, EntityAttackMetadata entityAttackMetadata) {
      double amount = this.amount.get(data);
      String damageType = this.damageType == null ? null : this.damageType.get(data);
      if (damageType == null && !this.modAllDamage) {
         switch (this.modifier) {
            case ADDITIVE:
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying Physical Damage - Adding {0} ", amount);
               entityAttackMetadata.setDamage(entityAttackMetadata.getDamage() + amount);
               break;
            case ADDITIVE_MULTIPLIER: {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying Physical Damage - Adding Multiplier {0} ", amount);
               double current = entityAttackMetadata.getDamageMultiplier("PHYSICAL", 0.0);
               entityAttackMetadata.setDamageMultiplier("PHYSICAL", current + amount);
               break;
            }
            case COMPOUND_MULTIPLIER: {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying Physical Damage - Multiplying by {0} ", amount);
               double current = entityAttackMetadata.getDamageMultiplier("PHYSICAL", 1.0);
               entityAttackMetadata.setDamageMultiplier("PHYSICAL", current * amount);
               break;
            }
            case SETTER:
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying Physical Damage - Setting to {0} ", amount);
               entityAttackMetadata.setDamage(amount);
         }
      } else if (damageType != null) {
         switch (this.modifier) {
            case ADDITIVE:
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying DamageType {0} - Adding {1} ", damageType, amount);
               entityAttackMetadata.getBonusDamage().put(damageType, entityAttackMetadata.getBonusDamage().getOrDefault(damageType, 0.0) + amount);
               break;
            case ADDITIVE_MULTIPLIER: {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying DamageType {0} - Adding Multiplier {1} ", damageType, amount);
               double current = entityAttackMetadata.getDamageMultiplier(damageType, 0.0);
               entityAttackMetadata.setDamageMultiplier(damageType, current + amount);
               break;
            }
            case COMPOUND_MULTIPLIER: {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying DamageType {0} - Multiplying by {1} ", damageType, amount);
               double current = entityAttackMetadata.getDamageMultiplier(damageType, 1.0);
               entityAttackMetadata.setDamageMultiplier(damageType, current * amount);
               break;
            }
            case SETTER:
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying DamageType {0} - Setting to {1} ", damageType, amount);
               entityAttackMetadata.getBonusDamage().put(damageType, amount);
         }
      } else if (this.modAllDamage) {
         switch (this.modifier) {
            case ADDITIVE:
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying All Damage - Adding {0} ", amount);
               entityAttackMetadata.setDamage(entityAttackMetadata.getDamage() + amount);
               break;
            case ADDITIVE_MULTIPLIER:
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying All Damage - Adding Multiplier {0} ", amount);
               entityAttackMetadata.setDamageModifier(entityAttackMetadata.getDamageModifier() + amount);
               break;
            case COMPOUND_MULTIPLIER:
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying All Damage - Multiplying by {0} ", amount);
               entityAttackMetadata.setDamageModifier(entityAttackMetadata.getDamageModifier() * amount);
               break;
            case SETTER:
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Modifying All Damage - Setting to {0} ", amount);
               entityAttackMetadata.setDamageModifier(amount);
         }
      }
   }
}
