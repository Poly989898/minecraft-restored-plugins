package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractTaskManager;
import java.security.InvalidParameterException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitTaskManager implements AbstractTaskManager {
   private JavaPlugin plugin = null;

   public BukkitTaskManager(JavaPlugin plugin) throws InvalidParameterException {
      this.plugin = plugin;
   }

   @Override
   public int scheduleTask(Runnable task, int delay, int interval) {
      return Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> task.run(), delay, interval);
   }

   @Override
   public void cancelTask(int taskId) {
      Bukkit.getScheduler().cancelTask(taskId);
   }

   @Override
   public int scheduleAsyncTask(Runnable task, int delay, int interval) {
      return Bukkit.getScheduler().scheduleAsyncRepeatingTask(this.plugin, () -> task.run(), delay, interval);
   }

   @Override
   public void run(Runnable task) {
      Bukkit.getScheduler().runTask(this.plugin, task);
   }

   @Override
   public void runAsync(Runnable task) {
      Bukkit.getScheduler().runTaskAsynchronously(this.plugin, task);
   }

   @Override
   public void runAsyncLater(Runnable task, int delay) {
      Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, task, delay);
   }

   @Override
   public void runLater(Runnable task, int delay) {
      Bukkit.getScheduler().runTaskLater(this.plugin, task, delay);
   }
}
