package me.TechsCode.UltraPermissions.base.visual;

import net.md_5.bungee.api.ChatColor;

public class BungeeColor {
   public BungeeColor() {
   }

   public static String colorOf(String text) {
      return ChatColor.of(text) + "";
   }
}
