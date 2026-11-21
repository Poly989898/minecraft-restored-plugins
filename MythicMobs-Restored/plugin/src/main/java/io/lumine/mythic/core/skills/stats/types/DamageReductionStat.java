package io.lumine.mythic.core.skills.stats.types;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.stats.StatExecution;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.stats.PercentModifyer;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.stats.TriggerModifyingStat;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;

public class DamageReductionStat extends StatType implements TriggerModifyingStat, PercentModifyer {
   public DamageReductionStat(String key) {
      super(key);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);
      this.executionPoint = StatExecution.PRE;
      this.getApplicableTriggers().add(SkillTriggers.DAMAGED);
   }

   @Override
   public void processTrigger(SkillMetadata skillMetadata, SkillTriggerMetadata triggerMetadata, StatSnapshot statRegistry, double value) {
      if (triggerMetadata instanceof EntityAttackMetadata attackMetadata) {
         double damage = attackMetadata.getDamage();
         damage -= damage * value;
         if (damage < 0.0) {
            damage = 0.0;
         }

         attackMetadata.setDamage(damage);
      }
   }
}
