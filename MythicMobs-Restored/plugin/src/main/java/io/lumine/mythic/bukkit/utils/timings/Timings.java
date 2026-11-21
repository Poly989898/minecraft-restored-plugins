package io.lumine.mythic.bukkit.utils.timings;

import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import io.lumine.mythic.bukkit.utils.timingslib.TimingManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Timings {
   private static TimingManager timingManager = null;

   public static synchronized TimingManager get() {
      if (timingManager == null) {
         JavaPlugin plugin = LoaderUtils.getPlugin();
         timingManager = TimingManager.of(plugin);
      }

      return timingManager;
   }

   private Timings() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
