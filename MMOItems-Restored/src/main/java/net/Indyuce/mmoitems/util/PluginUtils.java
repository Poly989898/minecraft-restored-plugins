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

   public static void isDependencyPresent(@NotNull String text, @NotNull Consumer<Void> consumer) {
      if (Bukkit.getPluginManager().getPlugin(text) != null) {
         consumer.accept(null);
      }
   }

   public static void hookDependencyIfPresent(@NotNull String text, @NotNull Consumer<Void> consumer) {
      if (Bukkit.getPluginManager().getPlugin(text) != null) {
         consumer.accept(null);
         MMOItems.plugin.getLogger().log(Level.INFO, String.format("Hooked onto %s", text));
      }
   }
}
