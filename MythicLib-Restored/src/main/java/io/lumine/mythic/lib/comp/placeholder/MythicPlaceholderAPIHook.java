package io.lumine.mythic.lib.comp.placeholder;

import io.lumine.mythic.lib.api.placeholders.MythicPlaceholder;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicPlaceholderAPIHook implements MythicPlaceholder {
   @NotNull
   @Override
   public String getAuthorName() {
      return "gunging";
   }

   @NotNull
   @Override
   public String getMythicIdentifier() {
      return "";
   }

   @Nullable
   @Override
   public String parse(@NotNull String text, @NotNull Object thing) {
      return "";
   }

   @Override
   public boolean forUseWith(@NotNull Object obj) {
      return obj instanceof OfflinePlayer;
   }
}
