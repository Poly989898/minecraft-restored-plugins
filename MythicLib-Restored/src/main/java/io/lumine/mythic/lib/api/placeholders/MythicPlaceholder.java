package io.lumine.mythic.lib.api.placeholders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MythicPlaceholder {
   @NotNull
   String getAuthorName();

   @NotNull
   String getMythicIdentifier();

   @Nullable
   String parse(@NotNull String text, @NotNull Object obj);

   boolean forUseWith(@NotNull Object obj);
}
