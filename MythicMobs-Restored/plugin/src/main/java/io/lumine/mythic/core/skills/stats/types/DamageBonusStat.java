package io.lumine.mythic.core.skills.stats.types;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.stats.StatExecution;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringProp;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.stats.TriggerModifyingStat;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import lombok.Getter;

@Getter
public class DamageBonusStat extends StatType implements TriggerModifyingStat {
   private static final StringProp TYPE = Property.String(Scope.STATS, "DamageType", null);
   private String damageType;

   public DamageBonusStat(String key) {
      super(key);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);
      this.damageType = TYPE.fget(this.getScope(), this);
      this.executionPoint = StatExecution.PRE;
      this.getApplicableTriggers().add(SkillTriggers.ATTACK);
   }

   public DamageBonusStat(Object scope, String key) {
      super(scope, key);
      this.damageType = TYPE.fget(this.getScope(), this);
   }

   @Override
   public void processTrigger(SkillMetadata skillMetadata, SkillTriggerMetadata triggerMetadata, StatSnapshot statRegistry, double value) {
      if (triggerMetadata instanceof EntityAttackMetadata attackMetadata) {
         if (this.damageType == null) {
            attackMetadata.setDamage(attackMetadata.getDamage() + value);
         } else {
            Double number = attackMetadata.getBonusDamage().merge(this.damageType, value, (x, y) -> x + y);
         }
      }
   }
}
