package io.lumine.mythic.core.skills.stats.types;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.stats.StatExecution;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringProp;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.stats.TriggerModifyingStat;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;

public class DamageDefenseStat extends StatType implements TriggerModifyingStat {
   private static final StringProp TYPE = Property.String(Scope.STATS, "DamageType", null);
   private String damageType;

   public DamageDefenseStat(String key) {
      super(key);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);
      this.damageType = TYPE.fget(this.getScope(), this);
      this.executionPoint = StatExecution.PRE;
      this.getApplicableTriggers().add(SkillTriggers.DAMAGED);
   }

   @Override
   public void processTrigger(SkillMetadata skillMetadata, SkillTriggerMetadata triggerMetadata, StatSnapshot statRegistry, double value) {
      if (triggerMetadata instanceof EntityAttackMetadata attackMetadata) {
         if (this.damageType == null) {
            attackMetadata.setDamage(Numbers.max(attackMetadata.getDamage() - value, 0.0));
         } else {
            attackMetadata.getBonusDamage().compute(this.damageType, (d, v) -> v == null ? 0.0 : Numbers.max(v - value, 0.0));
         }
      }
   }
}
