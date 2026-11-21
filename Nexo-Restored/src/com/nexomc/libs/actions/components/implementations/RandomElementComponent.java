package com.nexomc.libs.actions.components.implementations;

import com.nexomc.libs.actions.actions.Context;
import com.nexomc.libs.actions.components.Component;
import com.nexomc.libs.actions.placeholders.PlaceholderManager;
import java.util.SplittableRandom;
import org.jetbrains.annotations.NotNull;

public class RandomElementComponent<T> extends Component<T, String> {
   private static final SplittableRandom RANDOM = new SplittableRandom();
   public static final String ID = "randomE";

   public RandomElementComponent(@NotNull String stringValue, @NotNull PlaceholderManager placeholderManager) {
      super(stringValue, placeholderManager);
   }

   public @NotNull String parse(@NotNull T t, @NotNull Context<T> context) {
      String[] elements = this.placeholderManager.replace(t, this.stringValue, context).split(",");
      int length = elements.length;
      return length == 1 ? elements[0] : elements[RANDOM.nextInt(length)];
   }
}
