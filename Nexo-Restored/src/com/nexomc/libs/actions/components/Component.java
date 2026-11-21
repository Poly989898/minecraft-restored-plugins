package com.nexomc.libs.actions.components;

import com.nexomc.libs.actions.actions.Context;
import com.nexomc.libs.actions.placeholders.PlaceholderManager;
import org.jetbrains.annotations.NotNull;

public abstract class Component<T, R> {
   protected final String stringValue;
   protected final PlaceholderManager placeholderManager;

   public Component(@NotNull String stringValue, @NotNull PlaceholderManager placeholderManager) {
      this.stringValue = stringValue;
      this.placeholderManager = placeholderManager;
   }

   public abstract @NotNull R parse(@NotNull T var1, @NotNull Context<T> var2);

   @FunctionalInterface
   public interface Supplier<T, R> {
      Component<T, R> run(@NotNull String var1, @NotNull PlaceholderManager var2);
   }
}
