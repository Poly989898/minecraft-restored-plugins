package net.Indyuce.mmocore.loot.chest.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import org.apache.commons.lang.Validate;

public class WeatherCondition extends Condition {
   private final String condition;

   public WeatherCondition(MMOLineConfig config) {
      super(config);
      Validate.isTrue(config.contains("condition"));
      this.condition = config.getString("condition");
   }

   @Override
   public boolean isMet(ConditionInstance entity) {
      boolean isClear = entity.getLocation().getWorld().isClearWeather();
      boolean hasStorm = entity.getLocation().getWorld().hasStorm();
      if (this.condition.equalsIgnoreCase("clear")) {
         return isClear;
      } else {
         return this.condition.equalsIgnoreCase("stormy") ? hasStorm : false;
      }
   }
}
