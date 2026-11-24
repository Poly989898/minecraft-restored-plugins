package net.Indyuce.mmocore.loot.chest.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import org.apache.commons.lang.Validate;

public class TimeCondition extends Condition {
   private final int min;
   private final int max;

   public TimeCondition(MMOLineConfig config) {
      super(config);
      Validate.isTrue(config.contains("min"));
      Validate.isTrue(config.contains("max"));
      this.min = config.getInt("min");
      this.max = config.getInt("max");
   }

   @Override
   public boolean isMet(ConditionInstance entity) {
      long time = entity.getLocation().getWorld().getTime();
      return this.min < this.max ? time > this.min && time < this.max : time > this.min || time < this.max;
   }
}
