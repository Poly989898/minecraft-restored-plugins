package me.TechsCode.UltraPermissions.base.util;

import java.util.concurrent.CompletableFuture;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.debug.TechException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class Async {
   public Async() {
   }

   public static <T extends Runnable> BukkitTask runLater(T task) {
      return runLater(1, task);
   }

   public static BukkitTask runLater(int delay, Runnable runnable) {
      BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
      JavaPlugin javaPlugin = (JavaPlugin)TechPlugin.getInstance().getBootstrap();

      try {
         return runIfDisabled(runnable)
            ? null
            : (
               delay == 0
                  ? (runnable instanceof BukkitRunnable ? ((BukkitRunnable)runnable).runTask(javaPlugin) : bukkitScheduler.runTask(javaPlugin, runnable))
                  : (runnable instanceof BukkitRunnable ? ((BukkitRunnable)runnable).runTaskLater(javaPlugin, delay) : bukkitScheduler.runTaskLater(javaPlugin, runnable, delay))
            );
      } catch (NoSuchMethodError ex) {
         return runIfDisabled(runnable)
            ? null
            : (
               delay == 0
                  ? (runnable instanceof BukkitRunnable ? ((BukkitRunnable)runnable).runTask(javaPlugin) : getTaskFromId(bukkitScheduler.scheduleSyncDelayedTask(javaPlugin, runnable)))
                  : (
                     runnable instanceof BukkitRunnable
                        ? ((BukkitRunnable)runnable).runTaskLater(javaPlugin, delay)
                        : getTaskFromId(bukkitScheduler.scheduleSyncDelayedTask(javaPlugin, runnable, delay))
                  )
            );
      }
   }

   public static BukkitTask runAsync(Runnable runnable) {
      return runLaterAsync(0, runnable);
   }

   public static BukkitTask runLaterAsync(Runnable runnable) {
      return runLaterAsync(0, runnable);
   }

   public static BukkitTask runLaterAsync(int delay, Runnable runnable) {
      BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
      JavaPlugin javaPlugin = (JavaPlugin)TechPlugin.getInstance().getBootstrap();

      try {
         return runIfDisabled(runnable)
            ? null
            : (
               delay == 0
                  ? (runnable instanceof BukkitRunnable ? ((BukkitRunnable)runnable).runTaskAsynchronously(javaPlugin) : bukkitScheduler.runTaskAsynchronously(javaPlugin, runnable))
                  : (
                     runnable instanceof BukkitRunnable
                        ? ((BukkitRunnable)runnable).runTaskLaterAsynchronously(javaPlugin, delay)
                        : bukkitScheduler.runTaskLaterAsynchronously(javaPlugin, runnable, delay)
                  )
            );
      } catch (NoSuchMethodError ex) {
         return runIfDisabled(runnable)
            ? null
            : (delay == 0 ? getTaskFromId(bukkitScheduler.scheduleAsyncDelayedTask(javaPlugin, runnable)) : getTaskFromId(bukkitScheduler.scheduleAsyncDelayedTask(javaPlugin, runnable, delay)));
      }
   }

   public static BukkitTask runTimer(int period, Runnable runnable) {
      return runTimer(0, period, runnable);
   }

   public static BukkitTask runTimer(int delay, int period, Runnable runnable) {
      try {
         return runIfDisabled(runnable)
            ? null
            : (
               runnable instanceof BukkitRunnable
                  ? ((BukkitRunnable)runnable).runTaskTimer((JavaPlugin)TechPlugin.getInstance().getBootstrap(), delay, period)
                  : Bukkit.getScheduler().runTaskTimer((JavaPlugin)TechPlugin.getInstance().getBootstrap(), runnable, delay, period)
            );
      } catch (NoSuchMethodError ex) {
         return runIfDisabled(runnable)
            ? null
            : getTaskFromId(Bukkit.getScheduler().scheduleSyncRepeatingTask((JavaPlugin)TechPlugin.getInstance().getBootstrap(), runnable, delay, period));
      }
   }

   public static BukkitTask runTimerAsync(int period, Runnable runnable) {
      return runTimerAsync(0, period, runnable);
   }

   public static BukkitTask runTimerAsync(int delay, int period, Runnable runnable) {
      try {
         return runIfDisabled(runnable)
            ? null
            : (
               runnable instanceof BukkitRunnable
                  ? ((BukkitRunnable)runnable).runTaskTimerAsynchronously((JavaPlugin)TechPlugin.getInstance().getBootstrap(), delay, period)
                  : Bukkit.getScheduler().runTaskTimerAsynchronously((JavaPlugin)TechPlugin.getInstance().getBootstrap(), runnable, delay, period)
            );
      } catch (NoSuchMethodError ex) {
         return runIfDisabled(runnable)
            ? null
            : getTaskFromId(Bukkit.getScheduler().scheduleAsyncRepeatingTask((JavaPlugin)TechPlugin.getInstance().getBootstrap(), runnable, delay, period));
      }
   }

   private static BukkitTask getTaskFromId(int taskId) {
      for (BukkitTask bukkitTask : Bukkit.getScheduler().getPendingTasks()) {
         if (bukkitTask.getTaskId() == taskId) {
            return bukkitTask;
         }
      }

      return null;
   }

   private static boolean runIfDisabled(Runnable runnable) {
      if (!((JavaPlugin)TechPlugin.getInstance().getBootstrap()).isEnabled()) {
         runnable.run();
         return true;
      } else {
         return false;
      }
   }

   public static <T> void runAsyncWithCallbacks(Async.AsyncTask<T> asyncTask, Async.AsyncCallback<T> asyncCallback, Async.ErrorCallback errorCallback) {
      runAsync(() -> {
         CompletableFuture<T> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
               TechPlugin.getInstance().log("Running async task to give something back");
               return asyncTask.runAsync();
            } catch (Exception ex) {
               if (errorCallback != null) {
                  errorCallback.onError(ex);
               }

               return null;
            }
         });
         completableFuture.whenComplete((result, ex) -> {
            if (ex != null) {
               if (errorCallback != null) {
                  errorCallback.onError((Exception)ex);
               }
            } else if (asyncCallback != null) {
               asyncCallback.onComplete(result);
            }
         });
      });
   }

   public static void runAsyncWithCallbacks(Runnable runnable, Runnable callback, Async.ErrorCallback errorCallback) {
      runAsync(() -> {
         CompletableFuture completableFuture = CompletableFuture.runAsync(() -> {
            try {
               runnable.run();
            } catch (Exception ex) {
               if (errorCallback != null) {
                  errorCallback.onError(ex);
               }
            }
         });
         completableFuture.whenComplete((result, ex) -> {
            if (ex != null) {
               if (errorCallback != null) {
                  errorCallback.onError((Exception)ex);
               }
            } else if (callback != null) {
               callback.run();
            }
         });
      });
   }

   public static void checkSync(String message) {
      if (!Bukkit.isPrimaryThread()) {
         throw new TechException(message);
      }
   }

   public interface AsyncCallback<T> {
      void onComplete(T result);
   }

   public interface AsyncTask<T> {
      T runAsync() throws Exception;
   }

   public interface ErrorCallback {
      void onError(Exception exception);
   }
}
