package fr.phoenixdevt.profiles.placeholder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlaceholderCache {
   void registerPlaceholder(@NotNull String key, @NotNull String value);

   @Nullable
   String getPlaceholder(@NotNull String text);
}
