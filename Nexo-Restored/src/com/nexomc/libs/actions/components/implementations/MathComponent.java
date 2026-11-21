package com.nexomc.libs.actions.components.implementations;

import com.google.common.primitives.Ints;
import com.nexomc.libs.actions.actions.Context;
import com.nexomc.libs.actions.components.Component;
import com.nexomc.libs.actions.placeholders.PlaceholderManager;
import com.udojava.evalex.Expression;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.jetbrains.annotations.NotNull;

public class MathComponent<T> extends Component<T, BigDecimal> {
   public static final String ID = "math";
   private final int scale;
   private final String stringValue;

   public MathComponent(@NotNull String stringValue, @NotNull PlaceholderManager placeholderManager) {
      super(stringValue, placeholderManager);
      String[] parts = stringValue.split(",", 2);
      if (parts.length == 2) {
         Integer scale = Ints.tryParse(parts[0]);
         if (scale == null) {
            this.scale = -1;
            this.stringValue = stringValue;
         } else {
            this.scale = scale;
            this.stringValue = parts[1];
         }
      } else {
         this.scale = -1;
         this.stringValue = stringValue;
      }

   }

   public @NotNull BigDecimal parse(@NotNull T t, @NotNull Context<T> context) {
      BigDecimal result = (new Expression(this.placeholderManager.replace(t, this.stringValue, context))).eval(false);
      return this.scale < 0 ? result : result.setScale(this.scale, RoundingMode.HALF_UP);
   }
}
