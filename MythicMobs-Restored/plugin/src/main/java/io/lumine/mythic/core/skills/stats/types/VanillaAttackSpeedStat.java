package io.lumine.mythic.core.skills.stats.types;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.stats.StatExecution;
import io.lumine.mythic.core.skills.stats.AttributeMappedStat;
import io.lumine.mythic.core.skills.stats.StatType;

public class VanillaAttackSpeedStat extends StatType implements AttributeMappedStat {
   public VanillaAttackSpeedStat(String key) {
      super(key);
      this.executionPoint = StatExecution.NONE;
   }

   @Override
   public void applyAttribute(AbstractEntity entity, double newValue) {
      entity.setAttackSpeed(newValue);
   }
}
