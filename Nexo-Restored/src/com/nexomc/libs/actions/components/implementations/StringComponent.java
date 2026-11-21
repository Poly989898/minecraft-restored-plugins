package com.nexomc.libs.actions.components.implementations;

import com.nexomc.libs.actions.actions.Context;
import com.nexomc.libs.actions.components.Component;
import com.nexomc.libs.actions.placeholders.PlaceholderManager;
import org.jetbrains.annotations.NotNull;

public class StringComponent<T> extends Component<T, String> {
   public StringComponent(@NotNull String stringValue, @NotNull PlaceholderManager placeholderManager) {
      super(stringValue, placeholderManager);
   }

   public @NotNull String parse(@NotNull T t, @NotNull Context<T> context) {
      return this.placeholderManager.replace(t, this.stringValue, context);
   }
}
