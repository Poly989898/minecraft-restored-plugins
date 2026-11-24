package me.TechsCode.UltraPermissions.base.scheduler;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class SpigotScheduler implements Scheduler {
   private SpigotTechPlugin plugin;

   public SpigotScheduler(SpigotTechPlugin spigotTechPlugin) {
      this.plugin = spigotTechPlugin;
   }

   @Override
   public void run(Runnable task) {
      Bukkit.getScheduler().runTask((Plugin)this.plugin.getBootstrap(), task);
   }

   @Override
   public void runAsync(Runnable task) {
      Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin.getBootstrap(), task);
   }

   @Override
   public Task runTaskLater(Runnable task, long value) {
      BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater((Plugin)this.plugin.getBootstrap(), task, value);
      return this.toGenericTask(bukkitTask);
   }

   @Override
   public RecurringTask runTaskTimer(Runnable task, long value, long period) {
      return this.toRecurringTask(() -> Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin.getBootstrap(), task, value, period));
   }

   @Override
   public Task runTaskLaterAsync(Runnable task, long value) {
      BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)this.plugin.getBootstrap(), task, value);
      return this.toGenericTask(bukkitTask);
   }

   @Override
   public RecurringTask runTaskTimerAsync(Runnable task, long value, long period) {
      return this.toRecurringTask(() -> Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this.plugin.getBootstrap(), task, value, period));
   }

   private Task toGenericTask(final BukkitTask bukkitTask) {
      return new Task() {
         @Override
         public void cancel() {
            bukkitTask.cancel();
         }
      };
   }

   private RecurringTask toRecurringTask(final SpigotScheduler.TaskCreator taskCreator) {
      return new RecurringTask() {
         private BukkitTask bukkitTask = taskCreator.build();

         @Override
         public void stop() {
            if (this.bukkitTask != null) {
               this.bukkitTask.cancel();
               this.bukkitTask = null;
            }
         }

         @Override
         public void start() {
            if (this.bukkitTask == null) {
               this.bukkitTask = taskCreator.build();
            }
         }
      };
   }

   interface TaskCreator {
      BukkitTask build();
   }
}
