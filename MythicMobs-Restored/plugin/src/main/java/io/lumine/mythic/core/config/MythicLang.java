package io.lumine.mythic.core.config;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.locale.Language;
import org.bukkit.command.CommandSender;

public class MythicLang extends Language {
   private static MythicLang instance;

   private static MythicLang inst() {
      return instance;
   }

   public static String get(String key, String defaultValue, Object... args) {
      return instance.getString(key, defaultValue, args);
   }

   public static void send(CommandSender sender, String key, String defaultValue, Object... args) {
      String message = get(key, defaultValue, args);
      if (message != null && !message.isEmpty()) {
         CommandHelper.send(sender, message);
      }
   }

   public static void sendSuccess(CommandSender sender, String key, String defaultValue, Object... args) {
      String message = get(key, defaultValue, args);
      if (message != null && !message.isEmpty()) {
         CommandHelper.sendSuccess(sender, get(key, defaultValue, args));
      }
   }

   public static void sendError(CommandSender sender, String key, String defaultValue, Object... args) {
      String message = get(key, defaultValue, args);
      if (message != null && !message.isEmpty()) {
         CommandHelper.sendError(sender, get(key, defaultValue, args));
      }
   }

   public MythicLang(MythicBukkit plugin) {
      super(plugin, "config/lang", "mythicmobs");
      instance = this;
   }
}
