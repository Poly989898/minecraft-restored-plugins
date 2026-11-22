package net.Zrips.CMILib.Placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.commands.CommandsHandler;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PlaceholderExpansion {
  private CMILib plugin;

  public PlaceholderAPIHook(CMILib plugin) {
    this.plugin = plugin;
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
    return CommandsHandler.getLabel();
  }

  public String getVersion() {
    return this.plugin.getDescription().getVersion();
  }

  public String onPlaceholderRequest(Player player, String identifier) {
    if (player == null) {
      return "";
    }

    Placeholder.CMIPlaceHolders placeHolder =
        Placeholder.CMIPlaceHolders.getByName(
            "%" + CommandsHandler.getLabel() + "_" + identifier + "%");
    return placeHolder == null
        ? null
        : this.plugin
            .getPlaceholderAPIManager()
            .translateOwnPlaceHolder(player, "%cmi_" + identifier + "%");
  }
}
