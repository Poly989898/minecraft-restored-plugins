package io.lumine.mythic.bukkit.utils.chat;

import org.bukkit.ChatColor;

public class ColorString {
   public static String get(String s) {
      return ChatColor.translateAlternateColorCodes('&', s);
   }
}
