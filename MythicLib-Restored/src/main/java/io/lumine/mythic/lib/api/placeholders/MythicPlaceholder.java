package io.lumine.mythic.lib.api.placeholders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MythicPlaceholder {
   @NotNull
   String getAuthorName();

   @NotNull
   String getMythicIdentifier();

   @Nullable
   String parse(@NotNull String var1, @NotNull Object var2);

   boolean forUseWith(@NotNull Object var1);
}
