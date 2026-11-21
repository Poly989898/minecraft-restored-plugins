package com.nexomc.libs.actions.actions;

import org.jetbrains.annotations.NotNull;

public abstract class Action<T> {
   private final ActionMeta<T> meta;

   public Action(@NotNull ActionMeta<T> meta) {
      this.meta = meta;
   }

   public final @NotNull ActionMeta<T> getMeta() {
      return this.meta;
   }

   public abstract void run(@NotNull T var1, @NotNull Context<T> var2);

   @FunctionalInterface
   public interface Supplier<T> {
      Action<T> run(ActionMeta<T> var1);
   }
}
