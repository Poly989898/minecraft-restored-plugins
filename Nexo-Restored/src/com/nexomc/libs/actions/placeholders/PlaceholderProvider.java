package com.nexomc.libs.actions.placeholders;

import org.jetbrains.annotations.NotNull;

public interface PlaceholderProvider<T> {
   @NotNull Class<T> getType();

   @NotNull String replace(@NotNull T var1, @NotNull String var2);
}
