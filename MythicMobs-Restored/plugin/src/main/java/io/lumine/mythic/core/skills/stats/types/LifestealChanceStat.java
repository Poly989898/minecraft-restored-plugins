package io.lumine.mythic.core.skills.stats.types;

import io.lumine.mythic.api.adapters.AbstractEntity;
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

public class LifestealChanceStat extends StatType implements PercentModifyer, TriggerModifyingStat {
   public LifestealChanceStat(String key) {
      super(key);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);
      this.executionPoint = StatExecution.POST;
      this.getApplicableTriggers().add(SkillTriggers.ATTACK);
   }

   @Override
   public void processTrigger(SkillMetadata skillMetadata, SkillTriggerMetadata triggerMetadata, StatSnapshot statRegistry, double value) {
      if (triggerMetadata instanceof EntityAttackMetadata attackMetadata) {
         double roll = Numbers.randomDouble();
         if (value >= roll) {
            Optional<StatRegistry> maybeRegistry = this.getManager().getStatRegistry(attackMetadata.getDamager());
            double lifesteal = maybeRegistry.get().get(Stats.LIFESTEAL_POWER);
            double damage = attackMetadata.getFinalDamage();
            double heal = damage * lifesteal;
            AbstractEntity caster = skillMetadata.getCaster().getEntity();
            double hp = caster.getHealth() + heal;
            if (hp > caster.getMaxHealth()) {
               caster.setHealth(caster.getMaxHealth());
            } else {
               caster.setHealth(hp);
            }

            attackMetadata.putBoolean("lifesteal", true);
            this.runProcSkills(skillMetadata, triggerMetadata);
         }
      }
   }
}
