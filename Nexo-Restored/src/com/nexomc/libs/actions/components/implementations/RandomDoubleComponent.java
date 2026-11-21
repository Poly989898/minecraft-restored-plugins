package com.nexomc.libs.actions.components.implementations;

import com.nexomc.libs.actions.actions.Context;
import com.nexomc.libs.actions.components.Component;
import com.nexomc.libs.actions.placeholders.PlaceholderManager;
import com.nexomc.libs.actions.utils.Numbers;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.SplittableRandom;
import org.jetbrains.annotations.NotNull;

public class RandomDoubleComponent<T> extends Component<T, Double> {
   private static final SplittableRandom RANDOM = new SplittableRandom();
   public static final String ID = "random";
   private final String minString;
   private final String maxString;
   private final Double minDouble;
   private final Double maxDouble;
   private final int precision;

   public RandomDoubleComponent(@NotNull String stringValue, @NotNull PlaceholderManager placeholderManager) {
      super(stringValue, placeholderManager);
      String[] parts = stringValue.split(",");
      if (parts.length == 3) {
         this.precision = Numbers.tryParse(parts[0], -1);
         this.minString = parts[1];
         this.maxString = parts[2];
      } else {
         this.precision = -1;
         this.minString = parts[0];
         this.maxString = parts[1];
      }

      this.minDouble = Numbers.tryParseDouble(this.minString);
      this.maxDouble = Numbers.tryParseDouble(this.maxString);
   }

   public @NotNull Double parse(@NotNull T t, @NotNull Context<T> context) {
      double result;
      if (this.minDouble != null && this.maxDouble != null) {
         result = this.minDouble.equals(this.maxDouble) ? this.minDouble : RANDOM.nextDouble(this.minDouble, this.maxDouble + (double)1.0F);
      } else {
         double min = Numbers.tryParse(this.placeholderManager.replace(t, this.minString, context), (double)0.0F);
         double max = Numbers.tryParse(this.placeholderManager.replace(t, this.maxString, context), (double)0.0F);
         result = min == max ? min : RANDOM.nextDouble(min, max + (double)1.0F);
      }

      return this.precision < 0 ? result : BigDecimal.valueOf(result).setScale(this.precision, RoundingMode.HALF_UP).doubleValue();
   }
}
