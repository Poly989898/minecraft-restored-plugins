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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DodgeChanceStat extends StatType implements PercentModifyer, TriggerModifyingStat {
   public DodgeChanceStat(String key) {
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
         if (attackMetadata.getEvent().getCause() != DamageCause.ENTITY_ATTACK) {
            return;
         }

         Optional<StatRegistry> maybeRegistry = this.getManager().getStatRegistry(attackMetadata.getDamager());
         double accuracy;
         if (maybeRegistry.isPresent()) {
            accuracy = maybeRegistry.get().get(Stats.DODGE_NEGATION);
         } else {
            accuracy = 0.0;
         }

         double roll = Numbers.randomDouble();
         if (value - accuracy >= roll) {
            attackMetadata.setDamage(0.0);
            attackMetadata.putBoolean("dodge", true);
            this.runProcSkills(skillMetadata, triggerMetadata);
         }
      }
   }
}
