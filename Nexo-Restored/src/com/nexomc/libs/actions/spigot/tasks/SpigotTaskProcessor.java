package com.nexomc.libs.actions.spigot.tasks;

import com.nexomc.libs.actions.tasks.TaskProcessor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SpigotTaskProcessor implements TaskProcessor {
   private final JavaPlugin plugin;

   public SpigotTaskProcessor(JavaPlugin plugin) {
      this.plugin = plugin;
   }

   public void runAsync(@NotNull Runnable task) {
      Bukkit.getScheduler().runTaskAsynchronously(this.plugin, task);
   }

   public void runAsync(@NotNull Runnable task, long delay) {
      Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, task, delay);
   }

   public void runSync(@NotNull Runnable task) {
      if (Bukkit.isPrimaryThread()) {
         task.run();
      } else {
         Bukkit.getScheduler().runTask(this.plugin, task);
      }
   }

   public void runSync(@NotNull Runnable task, long delay) {
      Bukkit.getScheduler().runTaskLater(this.plugin, task, delay);
   }
}
