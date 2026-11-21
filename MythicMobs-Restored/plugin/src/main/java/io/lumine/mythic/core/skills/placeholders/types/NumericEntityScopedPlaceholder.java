package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import javax.annotation.Nullable;

public abstract class NumericEntityScopedPlaceholder extends EntityScopedPlaceholder<Double> {
   private int rounding = 2;

   public NumericEntityScopedPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
      if (context.config != null) {
         this.rounding = context.config.getInteger(new String[]{"rounding", "round", "r"}, 2);
      }
   }

   public NumericEntityScopedPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context, int rounding) {
      super(context);
      if (context.config == null) {
         this.rounding = rounding;
      } else {
         this.rounding = context.config.getInteger(new String[]{"rounding", "round", "r"}, rounding);
      }
   }

   @Nullable
   public Double applyToScope(PlaceholderContext placeholderContext) {
      Number ret = this.applyToScopeWithNumericFormatting(placeholderContext);
      return ret != null ? ret.doubleValue() : null;
   }

   @Nullable
   public abstract Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext);

   public String convertType(Double parsedPlaceholder) {
      if (this.rounding < 0) {
         return String.valueOf(parsedPlaceholder);
      } else {
         return this.rounding == 0
            ? String.valueOf(Numbers.toInt(Numbers.round(parsedPlaceholder, this.rounding)))
            : String.valueOf(Numbers.round(parsedPlaceholder, this.rounding));
      }
   }

   @Override
   public Class<Double> getParsedValueClass() {
      return Double.class;
   }
}
