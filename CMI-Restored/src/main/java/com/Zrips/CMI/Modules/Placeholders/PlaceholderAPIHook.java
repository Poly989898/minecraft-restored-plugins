package com.Zrips.CMI.Modules.Placeholders;

import com.Zrips.CMI.CMI;
import java.util.UUID;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {
  private CMI plugin;

  public PlaceholderAPIHook(CMI cmi) {
    this.plugin = cmi;
  }

  public boolean persist() {
    return true;
  }

  public boolean canRegister() {
    return true;
  }

  public String getAuthor() {
    return this.plugin.getDescription().getAuthors().toString();
  }

  public String getIdentifier() {
    return "cmi";
  }

  public String getVersion() {
    return this.plugin.getDescription().getVersion();
  }

  public String onPlaceholderRequest(Player player, @NotNull String string) {
    return player == null ? null : this.process(player.getUniqueId(), string);
  }

  public String onRequest(OfflinePlayer offlinePlayer, @NotNull String string) {
    return offlinePlayer == null ? null : this.process(offlinePlayer.getUniqueId(), string);
  }

  private String process(UUID uuid, String string) {
    Placeholder.CMIPlaceHolders cmiPlaceHolders =
        Placeholder.CMIPlaceHolders.getByName("%cmi_" + string + "%");
    if (cmiPlaceHolders != null || uuid == null) {
      return this.plugin
          .getPlaceholderAPIManager()
          .translatePAPIPlaceHolder(uuid, cmiPlaceHolders, "%cmi_" + string + "%");
    } else {
      return isMetaPlaceHolder(string)
          ? this.plugin
              .getPlayerManager()
              .getUser(uuid)
              .getMeta()
              .getValue(string.substring("user_meta_".length(), string.length()))
          : null;
    }
  }

  private static boolean isMetaPlaceHolder(String string) {
    return string.startsWith("user_meta_");
  }
}
