package io.lumine.mythic.core.skills.stats.types;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.stats.StatExecution;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.stats.PercentModifyer;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.stats.Stats;
import io.lumine.mythic.core.skills.stats.TriggerModifyingStat;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import java.util.Optional;

public class CriticalStrikeChanceStat extends StatType implements PercentModifyer, TriggerModifyingStat {
   public CriticalStrikeChanceStat(String key) {
      super(key);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);
      this.executionPoint = StatExecution.PRE;
      this.getApplicableTriggers().add(SkillTriggers.ATTACK);
   }

   @Override
   public void processTrigger(SkillMetadata skillMetadata, SkillTriggerMetadata triggerMetadata, StatSnapshot statRegistry, double value) {
      if (triggerMetadata instanceof EntityAttackMetadata attackMetadata) {
         Optional<StatRegistry> maybeRegistry = this.getManager().getStatRegistry(attackMetadata.getDamaged());
         double resistance;
         if (maybeRegistry.isPresent()) {
            resistance = maybeRegistry.get().get(Stats.CRITICAL_STRIKE_RESILIENCE);
         } else {
            resistance = 0.0;
         }

         double roll = Numbers.randomDouble();
         if (value - resistance >= roll) {
            double bonus = 1.0 + statRegistry.get(Stats.CRITICAL_STRIKE_DAMAGE);
            attackMetadata.addCompoundMultiplier(bonus);
            attackMetadata.putBoolean("critical_strike", true);
            this.runProcSkills(skillMetadata, triggerMetadata);
         }
      }
   }
}
