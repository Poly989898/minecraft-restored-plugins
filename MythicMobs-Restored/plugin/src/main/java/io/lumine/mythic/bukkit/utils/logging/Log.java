package io.lumine.mythic.bukkit.utils.logging;

import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import java.util.logging.Level;

public final class Log {
   public static void info(String s) {
      LoaderUtils.getPlugin().getLogger().log(Level.INFO, s);
   }

   public static void info(String s, Object... params) {
      LoaderUtils.getPlugin().getLogger().log(Level.INFO, s, params);
   }

   public static void info(ConsoleColor color, String s) {
      LoaderUtils.getPlugin().getLogger().log(Level.INFO, color + s + ConsoleColor.WHITE);
   }

   public static void info(ConsoleColor color, String s, Object... params) {
      LoaderUtils.getPlugin().getLogger().log(Level.INFO, String.format("%s%s%s", color, s, ConsoleColor.WHITE), params);
   }

   public static void warn(String s) {
      LoaderUtils.getPlugin().getLogger().log(Level.WARNING, ConsoleColor.YELLOW + s + ConsoleColor.WHITE);
   }

   public static void warn(String s, Object... params) {
      LoaderUtils.getPlugin().getLogger().log(Level.WARNING, ConsoleColor.YELLOW + s + ConsoleColor.WHITE);
   }

   public static void severe(String s) {
      LoaderUtils.getPlugin().getLogger().log(Level.SEVERE, ConsoleColor.RED + s + ConsoleColor.WHITE);
   }

   public static void severe(String s, Exception ex) {
      LoaderUtils.getPlugin().getLogger().log(Level.SEVERE, ConsoleColor.RED + s + ConsoleColor.WHITE);
      ex.printStackTrace();
   }

   public static void error(String s) {
      LoaderUtils.getPlugin().getLogger().log(Level.SEVERE, ConsoleColor.RED + s + ConsoleColor.WHITE);
   }

   public static void error(String s, Object... params) {
      LoaderUtils.getPlugin().getLogger().log(Level.SEVERE, ConsoleColor.RED + s + ConsoleColor.WHITE, params);
   }

   public static void good(String s) {
      LoaderUtils.getPlugin().getLogger().log(Level.INFO, ConsoleColor.GREEN + s + ConsoleColor.WHITE);
   }

   public static void good(String s, Object... params) {
      LoaderUtils.getPlugin().getLogger().log(Level.INFO, ConsoleColor.GREEN + s + ConsoleColor.WHITE, params);
   }

   private Log() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
