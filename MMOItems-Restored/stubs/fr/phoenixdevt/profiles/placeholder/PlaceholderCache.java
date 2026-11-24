package fr.phoenixdevt.profiles.placeholder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlaceholderCache {
   void registerPlaceholder(@NotNull String var1, @NotNull String var2);

   @Nullable
   String getPlaceholder(@NotNull String var1);
}
