package io.lumine.mythic.bukkit.utils.plugin;

import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class LoaderUtils {
   private static LuminePlugin plugin = null;
   private static volatile Thread mainThread = null;

   public static synchronized LuminePlugin getPlugin() {
      if (plugin == null) {
         plugin = (LuminePlugin)JavaPlugin.getProvidingPlugin(LoaderUtils.class);
         String thisClass = LoaderUtils.class.getName();
         thisClass = thisClass.substring(0, thisClass.length() - ".utils.LoaderUtils".length());
         Bukkit.getLogger().info("[LumineUtils] (" + thisClass + ") is bound to plugin " + plugin.getName() + " - " + plugin.getClass().getName());
      }

      return plugin;
   }

   public static synchronized LuminePlugin getPlugin(Object obj) {
      return (LuminePlugin)JavaPlugin.getProvidingPlugin(obj.getClass());
   }

   @Nonnull
   public static Thread getMainThread() {
      Thread resolved = mainThread;
      return resolved != null ? resolved : resolveMainThread();
   }

   private static synchronized Thread resolveMainThread() {
      if (mainThread == null && Bukkit.getServer().isPrimaryThread()) {
         mainThread = Thread.currentThread();
      }

      return mainThread;
   }

   private LoaderUtils() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
