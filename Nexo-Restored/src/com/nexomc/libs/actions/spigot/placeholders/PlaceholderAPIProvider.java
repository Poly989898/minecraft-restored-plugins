package com.nexomc.libs.actions.spigot.placeholders;

import com.nexomc.libs.actions.placeholders.PlaceholderProvider;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIProvider implements PlaceholderProvider<OfflinePlayer> {
   public @NotNull Class<OfflinePlayer> getType() {
      return OfflinePlayer.class;
   }

   public @NotNull String replace(@NotNull OfflinePlayer player, @NotNull String input) {
      return PlaceholderAPI.setPlaceholders(player, input);
   }
}
