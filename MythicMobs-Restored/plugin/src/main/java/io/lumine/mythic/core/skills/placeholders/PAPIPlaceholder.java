package io.lumine.mythic.core.skills.placeholders;

import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

@Getter
public abstract class PAPIPlaceholder<T extends LuminePlugin> {
   private T plugin;

   public PAPIPlaceholder(T plugin) {
      this.plugin = plugin;
   }

   public abstract String onPlaceholderRequest(OfflinePlayer offlinePlayer, String[] strs);
}
