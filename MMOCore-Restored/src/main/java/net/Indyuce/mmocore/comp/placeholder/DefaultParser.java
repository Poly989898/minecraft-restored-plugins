package net.Indyuce.mmocore.comp.placeholder;

import io.lumine.mythic.lib.MythicLib;
import org.bukkit.OfflinePlayer;

public class DefaultParser implements PlaceholderParser {
   @Override
   public String parse(OfflinePlayer player, String string) {
      return MythicLib.plugin.parseColors(string.replace("%player%", player.getName()));
   }
}
