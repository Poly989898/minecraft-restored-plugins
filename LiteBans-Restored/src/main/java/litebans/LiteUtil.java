package litebans;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class LiteUtil {
   private static final Runnable a_f = () -> {

   };

   public static void access$getServer(BukkitPlugin bukkitPlugin, Runnable runnable) {
      bukkitPlugin.getServer().getGlobalRegionScheduler().execute(bukkitPlugin, runnable);
   }

   public static void handleBukkitPlugin(BukkitPlugin bukkitPlugin, LiteSender liteSender, Runnable runnable) {
      ((Entity)liteSender.getHandle()).getScheduler().execute(bukkitPlugin, runnable, a_f, 0L);
   }

   public static void access$getServer2(BukkitPlugin bukkitPlugin, Location location, Runnable runnable) {
      bukkitPlugin.getServer().getRegionScheduler().execute(bukkitPlugin, location, runnable);
   }

}
