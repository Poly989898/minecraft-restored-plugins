package io.lumine.mythic.core.skills.stats.types;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.stats.StatExecution;
import io.lumine.mythic.core.skills.stats.AttributeMappedStat;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatType;

public class VanillaBlockInteractionRangeStat extends StatType implements AttributeMappedStat {
   public VanillaBlockInteractionRangeStat(String key) {
      super(key);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);
      this.executionPoint = StatExecution.NONE;
   }

   @Override
   public void applyAttribute(AbstractEntity entity, double newValue) {
      entity.setBlockInteractionRange(newValue);
   }
}
