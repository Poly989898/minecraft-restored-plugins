package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.triggers.meta.DamagedMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "DamageCause",
   aliases = {},
   version = "4.7",
   description = "Checks the damage cause of the current skill tree. Only works with onDamaged trigger or aura."
)
public class DamageCauseCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "damagecause",
      aliases = {"cause", "c"},
      description = "The damage cause to match"
   )
   protected PlaceholderString cause;

   public DamageCauseCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.cause = mlc.getPlaceholderString(new String[]{"damagecause", "cause", "c"}, "ENTITY_ATTACK", this.conditionVar);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      if (meta.getTriggerMetadata() != null) {
         if (meta.getTriggerMetadata() instanceof EntityAttackMetadata attackMetadata) {
            String cause = attackMetadata.getDamageCause();
            String checkCause = this.cause.get(meta);
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "DamageCause condition checking {0} == {1}", cause, checkCause);
            return checkCause.equalsIgnoreCase(cause);
         }

         if (meta.getTriggerMetadata() instanceof DamagedMetadata damagedMetadata) {
            String cause = damagedMetadata.getDamageCause();
            String checkCause = this.cause.get(meta);
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "DamageCause condition checking {0} == {1}", cause, checkCause);
            return checkCause.equalsIgnoreCase(cause);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "DamageCause condition failed: no damage metadata present");
      } else {
         if (meta.getVariables().has("damage-cause")) {
            String cause = (String)meta.getVariables().get("damage-cause").get();
            String checkCause = this.cause.get(meta);
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "DamageCause condition checking {0} == {1}", cause, checkCause);
            return checkCause.equalsIgnoreCase(cause);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "DamageCause condition failed: no damage metadata present");
      }

      return false;
   }
}
