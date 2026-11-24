package net.Indyuce.mmoitems.util;

import java.util.function.Consumer;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class PluginUtils {
   public PluginUtils() {
      throw new IllegalStateException("Utility class");
   }

   public static void isDependencyPresent(@NotNull String var0, @NotNull Consumer<Void> var1) {
      if (Bukkit.getPluginManager().getPlugin(var0) != null) {
         var1.accept(null);
      }
   }

   public static void hookDependencyIfPresent(@NotNull String var0, @NotNull Consumer<Void> var1) {
      if (Bukkit.getPluginManager().getPlugin(var0) != null) {
         var1.accept(null);
         MMOItems.plugin.getLogger().log(Level.INFO, String.format("Hooked onto %s", var0));
      }
   }
}
