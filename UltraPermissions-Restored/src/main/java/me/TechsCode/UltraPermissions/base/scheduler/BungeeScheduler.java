package me.TechsCode.UltraPermissions.base.scheduler;

import java.util.concurrent.TimeUnit;
import me.TechsCode.UltraPermissions.base.BungeeTechPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class BungeeScheduler implements Scheduler {
   private BungeeTechPlugin plugin;

   public BungeeScheduler(BungeeTechPlugin plugin) {
      this.plugin = plugin;
   }

   @Override
   public void run(Runnable runnable) {
      runnable.run();
   }

   @Override
   public void runAsync(Runnable runnable) {
      ProxyServer.getInstance().getScheduler().runAsync(this.plugin.getBootstrap(), runnable);
   }

   @Override
   public Task runTaskLater(Runnable runnable, long delay) {
      final ScheduledTask scheduledTask = ProxyServer.getInstance().getScheduler().schedule(this.plugin.getBootstrap(), runnable, delay * 1000L / 20L, TimeUnit.MILLISECONDS);
      return new Task() {
         @Override
         public void cancel() {
            scheduledTask.cancel();
         }
      };
   }

   @Override
   public RecurringTask runTaskTimer(final Runnable runnable, final long delay, final long period) {
      final ScheduledTask scheduledTask = ProxyServer.getInstance()
         .getScheduler()
         .schedule(this.plugin.getBootstrap(), runnable, delay * 1000L / 20L, period * 1000L / 20L, TimeUnit.MILLISECONDS);
      return new RecurringTask() {
         private boolean cancelled;

         @Override
         public void stop() {
            scheduledTask.cancel();
            this.cancelled = true;
         }

         @Override
         public void start() {
            if (this.cancelled) {
               BungeeScheduler.this.runTaskTimer(runnable, delay, period);
            }
         }
      };
   }

   @Override
   public Task runTaskLaterAsync(Runnable runnable, long delay) {
      throw new UnsupportedOperationException("The method runTaskLaterAsync is not defined for BungeeCord");
   }

   @Override
   public RecurringTask runTaskTimerAsync(Runnable runnable, long delay, long period) {
      throw new UnsupportedOperationException("The method runTaskTimerAsync is not defined for BungeeCord");
   }
}
