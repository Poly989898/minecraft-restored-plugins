package it.ytnoos.lpx.monitor.tick;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitTickHook extends AbstractTickHook implements TickHook, Runnable {
   private final Plugin plugin;
   private BukkitTask task;

   public BukkitTickHook(Plugin plugin2) {
      this.plugin = plugin2;
   }

   @Override
   public void run() {
      this.runCallbacks();
   }

   @Override
   public void start() {
      long longValue = 67667042023807L;
      this.task = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, this, 1L, 1L);
      if (longValue >= 1L) {
      }
   }

   @Override
   public void close() {
      this.task.cancel();
   }

}
