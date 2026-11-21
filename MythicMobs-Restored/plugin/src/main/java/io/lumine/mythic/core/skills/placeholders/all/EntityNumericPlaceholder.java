package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor;

@Deprecated
public class EntityNumericPlaceholder {
   protected final PlaceholderExecutor manager;
   private int rounding = -1;

   public EntityNumericPlaceholder(PlaceholderExecutor manager, MythicLineConfig config) {
      this.manager = manager;
      if (config != null) {
         this.rounding = config.getInteger(new String[]{"rounding", "round", "r"}, 2);
      }
   }

   public EntityNumericPlaceholder(PlaceholderExecutor manager, MythicLineConfig config, int rounding) {
      this.manager = manager;
      this.rounding = rounding;
      if (config != null) {
         this.rounding = config.getInteger(new String[]{"rounding", "round", "r"}, rounding);
      }
   }

   protected String format(Number number) {
      if (this.rounding < 0) {
         return String.valueOf(number.doubleValue());
      } else {
         return this.rounding == 0
            ? String.valueOf(Numbers.toInt(Numbers.round(number.doubleValue(), this.rounding)))
            : String.valueOf(Numbers.round(number.doubleValue(), this.rounding));
      }
   }
}
