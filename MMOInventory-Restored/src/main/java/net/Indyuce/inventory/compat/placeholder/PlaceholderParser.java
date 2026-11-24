package net.Indyuce.inventory.compat.placeholder;

import org.bukkit.OfflinePlayer;

public interface PlaceholderParser {
   String parse(OfflinePlayer offlinePlayer, String text);
}
