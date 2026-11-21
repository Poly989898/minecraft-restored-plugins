package com.nexomc.libs.actions.components.implementations;

import com.nexomc.libs.actions.actions.Context;
import com.nexomc.libs.actions.components.Component;
import com.nexomc.libs.actions.placeholders.PlaceholderManager;
import com.nexomc.libs.actions.utils.Numbers;
import java.util.SplittableRandom;
import org.jetbrains.annotations.NotNull;

public class RandomLongComponent<T> extends Component<T, Long> {
   private static final SplittableRandom RANDOM = new SplittableRandom();
   public static final String ID = "randomL";
   private final String minString;
   private final String maxString;
   private final Long minLong;
   private final Long maxLong;

   public RandomLongComponent(@NotNull String stringValue, @NotNull PlaceholderManager placeholderManager) {
      super(stringValue, placeholderManager);
      String[] parts = stringValue.split(",");
      this.minString = parts[0];
      this.maxString = parts[1];
      this.minLong = Numbers.tryParseLong(this.minString);
      this.maxLong = Numbers.tryParseLong(this.maxString);
   }

   public @NotNull Long parse(@NotNull T t, @NotNull Context<T> context) {
      if (this.minLong != null && this.maxLong != null) {
         return this.minLong.equals(this.maxLong) ? this.minLong : RANDOM.nextLong(this.minLong, this.maxLong + 1L);
      } else {
         long min = Numbers.tryParse(this.placeholderManager.replace(t, this.minString, context), 0L);
         long max = Numbers.tryParse(this.placeholderManager.replace(t, this.maxString, context), 0L);
         return min == max ? min : RANDOM.nextLong(min, max + 1L);
      }
   }
}
