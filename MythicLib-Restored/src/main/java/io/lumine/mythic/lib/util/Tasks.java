package io.lumine.mythic.lib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import org.jetbrains.annotations.NotNull;

public class Tasks {
   private static final List<Integer> ASYNC_SAFE_TASKS = new ArrayList<>();
   private static final List<Integer> SYNC_SAFE_TASKS = new ArrayList<>();
   private static final long ASYNC_TIME_OUT = 50L;

   public static boolean isSafeAsync(@NotNull BukkitTask task) {
      synchronized (ASYNC_SAFE_TASKS) {
         return ASYNC_SAFE_TASKS.contains(task.getTaskId());
      }
   }

   public static boolean isSafeSync(@NotNull BukkitTask task) {
      synchronized (SYNC_SAFE_TASKS) {
         return SYNC_SAFE_TASKS.contains(task.getTaskId());
      }
   }

   public static boolean isSafe(@NotNull BukkitTask task) {
      return isSafeSync(task) || isSafeAsync(task);
   }

   public static void executePendingSafe(@NotNull Plugin plugin) {
      for (BukkitTask pending : Bukkit.getScheduler().getPendingTasks()) {
         if (pending.getOwner().equals(plugin) && isSafe(pending)) {
            ((Runnable)pending).run();
         }
      }
   }

   public static void waitSafe(@NotNull Plugin plugin) {
      for (BukkitWorker worker : Bukkit.getScheduler().getActiveWorkers()) {
         if (worker.getOwner().equals(plugin)) {
            try {
               Thread.sleep(50L);
            } catch (InterruptedException var7) {
            } finally {
               continue;
            }
         }
      }
   }

   @NotNull
   public static CompletableFuture<Void> runSafeAsync(@NotNull Plugin plugin, @NotNull Runnable runnable) {
      CompletableFuture<Void> future = new CompletableFuture<>();
      BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
         try {
            runnable.run();
         } catch (Throwable throwable) {
            printStackTraceSync(plugin, throwable);
         }

         future.complete(null);
      });
      synchronized (ASYNC_SAFE_TASKS) {
         ASYNC_SAFE_TASKS.add(task.getTaskId());
      }

      return future.thenRun(() -> {
         synchronized (ASYNC_SAFE_TASKS) {
            ASYNC_SAFE_TASKS.remove(Integer.valueOf(task.getTaskId()));
         }
      });
   }

   private static void printStackTraceSync(@NotNull Plugin plugin, @NotNull Throwable throwable) {
      Bukkit.getScheduler().runTask(plugin, () -> {
         plugin.getLogger().log(Level.INFO, "Caught error on async thread:");
         throwable.printStackTrace();
      });
   }

   @NotNull
   public static CompletableFuture<Void> runAsync(@NotNull Plugin plugin, @NotNull Runnable runnable) {
      CompletableFuture<Void> future = new CompletableFuture<>();
      Bukkit.getScheduler().runTaskAsynchronously(plugin, task -> {
         try {
            runnable.run();
         } catch (Throwable throwable) {
            printStackTraceSync(plugin, throwable);
         }

         future.complete(null);
      });
      return future;
   }

   public static <T> Consumer<T> sync(@NotNull Plugin plugin, @NotNull Consumer<T> syncTask) {
      return t -> Bukkit.getScheduler().runTask(plugin, () -> syncTask.accept((T)t));
   }

   /** Runs the task right away on the main thread, or schedules it for the next tick from any other thread. */
   public static void runSync(@NotNull Plugin plugin, @NotNull Runnable runnable) {
      if (Bukkit.isPrimaryThread()) {
         runnable.run();
      } else {
         Bukkit.getScheduler().runTask(plugin, runnable);
      }
   }
}
