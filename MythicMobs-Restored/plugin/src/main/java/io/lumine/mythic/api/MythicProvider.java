package io.lumine.mythic.api;

public final class MythicProvider {
   private static MythicPlugin pluginInstance = null;

   public static MythicPlugin get() {
      if (pluginInstance == null) {
         throw new IllegalStateException("The Mythic plugin hasn't finished starting up yet, or failed to load!");
      } else {
         return pluginInstance;
      }
   }

   public static void register(MythicPlugin plugin) {
      pluginInstance = plugin;
   }
}
