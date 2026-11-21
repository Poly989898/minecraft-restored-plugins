package io.lumine.mythic.bukkit.utils.tasks;

import io.lumine.mythic.bukkit.utils.interfaces.Delegate;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import org.bukkit.Bukkit;

public final class LumineExecutors {
   private static final Consumer<Throwable> EXCEPTION_CONSUMER = throwable -> {
      Log.severe("[SCHEDULER] Exception thrown whilst executing task");
      throwable.printStackTrace();
   };
   private static final Executor SYNC_BUKKIT = new LumineExecutors.BukkitSyncExecutor();
   private static final Executor ASYNC_BUKKIT = new LumineExecutors.BukkitAsyncExecutor();
   private static final LumineAsyncExecutor ASYNC_LUMINE = new LumineAsyncExecutor();

   public static Executor sync() {
      return SYNC_BUKKIT;
   }

   public static ScheduledExecutorService asyncLumine() {
      return ASYNC_LUMINE;
   }

   public static Executor asyncBukkit() {
      return ASYNC_BUKKIT;
   }

   public static void shutdown() {
      ASYNC_LUMINE.cancelRepeatingTasks();
   }

   public static Runnable wrapRunnable(Runnable runnable) {
      return new LumineExecutors.SchedulerWrappedRunnable(runnable);
   }

   private LumineExecutors() {
   }

   private static final class BukkitAsyncExecutor implements Executor {
      @Override
      public void execute(Runnable runnable) {
         Bukkit.getScheduler().runTaskAsynchronously(LoaderUtils.getPlugin(), LumineExecutors.wrapRunnable(runnable));
      }
   }

   private static final class BukkitSyncExecutor implements Executor {
      @Override
      public void execute(Runnable runnable) {
         Bukkit.getScheduler().scheduleSyncDelayedTask(LoaderUtils.getPlugin(), LumineExecutors.wrapRunnable(runnable));
      }
   }

   private static final class SchedulerWrappedRunnable implements Runnable, Delegate<Runnable> {
      private final Runnable delegate;

      private SchedulerWrappedRunnable(Runnable delegate) {
         this.delegate = delegate;
      }

      @Override
      public void run() {
         try {
            this.delegate.run();
         } catch (Throwable e) {
            LumineExecutors.EXCEPTION_CONSUMER.accept(e);
         }
      }

      public Runnable getDelegate() {
         return this.delegate;
      }
   }
}
