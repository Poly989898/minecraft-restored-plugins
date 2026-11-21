package io.lumine.mythic.bukkit.utils;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.promise.ThreadContext;
import io.lumine.mythic.bukkit.utils.tasks.LumineExecutors;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.tasks.TaskScheduler;
import io.lumine.mythic.bukkit.utils.tasks.Ticks;
import io.lumine.mythic.bukkit.utils.tasks.builder.TaskBuilder;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

@NonnullByDefault
public final class Schedulers {
   private static final TaskScheduler SYNC_SCHEDULER = new Schedulers.SyncScheduler();
   private static final TaskScheduler ASYNC_SCHEDULER = new Schedulers.AsyncScheduler();
   private static final TaskScheduler PAPER_GLOBAL_SCHEDULER = new Schedulers.PaperGlobalScheduler();

   public static TaskScheduler get(ThreadContext context) {
      switch (context) {
         case SYNC:
            return sync();
         case ASYNC:
            return async();
         default:
            throw new AssertionError();
      }
   }

   public static TaskScheduler sync() {
      return ServerVersion.isFolia() ? PAPER_GLOBAL_SCHEDULER : SYNC_SCHEDULER;
   }

   public static TaskScheduler async() {
      return ASYNC_SCHEDULER;
   }

   public static TaskScheduler of(@Nonnull Entity entity) {
      Objects.requireNonNull(entity, "entity");
      return (TaskScheduler)(ServerVersion.isFolia() ? new Schedulers.PaperEntityScheduler(entity) : sync());
   }

   public static TaskScheduler of(@Nonnull Location location) {
      Objects.requireNonNull(location, "location");
      return (TaskScheduler)(ServerVersion.isFolia() ? new Schedulers.PaperRegionScheduler(location) : sync());
   }

   public static BukkitScheduler bukkit() {
      return Bukkit.getScheduler();
   }

   public static TaskBuilder builder() {
      return TaskBuilder.newBuilder();
   }

   public static void ensureSync(Runnable run) {
      Objects.requireNonNull(run, "run");
      if (ServerVersion.isFolia()) {
         sync().run(run);
      } else {
         if (!Bukkit.isPrimaryThread()) {
            sync().run(run);
         } else {
            run.run();
         }
      }
   }

   public static void ensureSync(@Nonnull Entity entity, @Nonnull Runnable run) {
      Objects.requireNonNull(entity, "entity");
      Objects.requireNonNull(run, "run");
      if (ServerVersion.isFolia()) {
         of(entity).run(run);
      } else {
         ensureSync(run);
      }
   }

   public static void ensureSync(@Nonnull Location location, @Nonnull Runnable run) {
      Objects.requireNonNull(location, "location");
      Objects.requireNonNull(run, "run");
      if (ServerVersion.isFolia()) {
         of(location).run(run);
      } else {
         ensureSync(run);
      }
   }

   public static void runOnRegion(@Nonnull Location location, @Nonnull Runnable run) {
      Objects.requireNonNull(location, "location");
      Objects.requireNonNull(run, "run");
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(location)) {
         of(location).run(run);
      } else {
         run.run();
      }
   }

   public static void runOnRegion(@Nonnull Entity entity, @Nonnull Runnable run) {
      Objects.requireNonNull(entity, "entity");
      Objects.requireNonNull(run, "run");
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity)) {
         of(entity).run(run);
      } else {
         run.run();
      }
   }

   public static void ensureAsync(Runnable run) {
      Objects.requireNonNull(run, "run");
      if (Bukkit.isPrimaryThread()) {
         async().run(run);
      } else {
         run.run();
      }
   }

   private Schedulers() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }

   private static final class AsyncScheduler implements TaskScheduler {
      @Override
      public void execute(Runnable runnable) {
         LumineExecutors.asyncLumine().execute(runnable);
      }

      @Nonnull
      @Override
      public ThreadContext getContext() {
         return ThreadContext.ASYNC;
      }

      @Nonnull
      @Override
      public Task runRepeating(@Nonnull Consumer<Task> consumer, long delayTicks, long intervalTicks) {
         Objects.requireNonNull(consumer, "consumer");
         long delayMillis = delayTicks * 50L;
         long intervalMillis = intervalTicks * 50L;
         return new Schedulers.LumineAsyncTask(consumer, delayMillis, TimeUnit.MILLISECONDS, intervalMillis, TimeUnit.MILLISECONDS);
      }

      @Nonnull
      @Override
      public Task runRepeating(@Nonnull Consumer<Task> consumer, long delay, @Nonnull TimeUnit delayUnit, long interval, @Nonnull TimeUnit intervalUnit) {
         Objects.requireNonNull(consumer, "consumer");
         return new Schedulers.LumineAsyncTask(consumer, delay, delayUnit, interval, intervalUnit);
      }
   }

   private static class LumineAsyncTask implements Runnable, Task {
      private final Consumer<Task> backingTask;
      private final ScheduledFuture<?> future;
      private final AtomicInteger counter = new AtomicInteger(0);
      private final AtomicBoolean cancelled = new AtomicBoolean(false);

      private LumineAsyncTask(Consumer<Task> backingTask, long delay, TimeUnit delayUnit, long interval, TimeUnit intervalUnit) {
         this.backingTask = backingTask;
         this.future = LumineExecutors.asyncLumine().scheduleAtFixedRate(this, delayUnit.toNanos(delay), intervalUnit.toNanos(interval), TimeUnit.NANOSECONDS);
      }

      @Override
      public void run() {
         if (!this.cancelled.get()) {
            try {
               this.backingTask.accept(this);
               this.counter.incrementAndGet();
            } catch (Throwable e) {
               Log.severe("[SCHEDULER] Exception thrown whilst executing task");
               e.printStackTrace();
            }
         }
      }

      @Override
      public int getTimesRan() {
         return this.counter.get();
      }

      @Override
      public boolean stop() {
         if (!this.cancelled.getAndSet(true)) {
            this.future.cancel(false);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public int getBukkitId() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasTerminated() {
         return this.cancelled.get();
      }
   }

   private static class LumineTask extends BukkitRunnable implements Task {
      private final Consumer<Task> backingTask;
      private final AtomicInteger counter = new AtomicInteger(0);
      private final AtomicBoolean cancelled = new AtomicBoolean(false);

      private LumineTask(Consumer<Task> backingTask) {
         this.backingTask = backingTask;
      }

      public void run() {
         if (this.cancelled.get()) {
            this.cancel();
         } else {
            try {
               this.backingTask.accept(this);
               this.counter.incrementAndGet();
            } catch (Throwable e) {
               Log.severe("[SCHEDULER] Exception thrown whilst executing task");
               e.printStackTrace();
            }

            if (this.cancelled.get()) {
               this.cancel();
            }
         }
      }

      @Override
      public int getTimesRan() {
         return this.counter.get();
      }

      @Override
      public boolean stop() {
         return !this.cancelled.getAndSet(true);
      }

      @Override
      public int getBukkitId() {
         return this.getTaskId();
      }

      @Override
      public boolean hasTerminated() {
         return this.cancelled.get();
      }
   }

   private static final class PaperEntityScheduler implements TaskScheduler {
      private final Entity entity;

      private PaperEntityScheduler(Entity entity) {
         this.entity = entity;
      }

      @Override
      public void execute(Runnable runnable) {
         this.entity.getScheduler().run(LoaderUtils.getPlugin(), task -> runnable.run(), null);
      }

      @Nonnull
      @Override
      public ThreadContext getContext() {
         return ThreadContext.SYNC;
      }

      @Nonnull
      @Override
      public <T> Promise<T> supply(@Nonnull Supplier<T> supplier) {
         Objects.requireNonNull(supplier, "supplier");
         return Promise.start(this.entity).thenApplySync(ignored -> supplier.get());
      }

      @Nonnull
      @Override
      public <T> Promise<T> call(@Nonnull Callable<T> callable) {
         Objects.requireNonNull(callable, "callable");
         return Promise.start(this.entity).thenApplySync(ignored -> {
            try {
               return callable.call();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         });
      }

      @Nonnull
      @Override
      public Promise<Void> run(@Nonnull Runnable runnable) {
         Objects.requireNonNull(runnable, "runnable");
         return Promise.start(this.entity).thenRunSync(runnable);
      }

      @Nonnull
      @Override
      public <T> Promise<T> supplyLater(@Nonnull Supplier<T> supplier, long delayTicks) {
         Objects.requireNonNull(supplier, "supplier");
         return Promise.start(this.entity).thenApplyDelayedSync(ignored -> supplier.get(), delayTicks);
      }

      @Nonnull
      @Override
      public <T> Promise<T> supplyLater(@Nonnull Supplier<T> supplier, long delay, @Nonnull TimeUnit unit) {
         Objects.requireNonNull(supplier, "supplier");
         return Promise.start(this.entity).thenApplyDelayedSync(ignored -> supplier.get(), delay, unit);
      }

      @Nonnull
      @Override
      public <T> Promise<T> callLater(@Nonnull Callable<T> callable, long delayTicks) {
         Objects.requireNonNull(callable, "callable");
         return Promise.start(this.entity).thenApplyDelayedSync(ignored -> {
            try {
               return callable.call();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }, delayTicks);
      }

      @Nonnull
      @Override
      public <T> Promise<T> callLater(@Nonnull Callable<T> callable, long delay, @Nonnull TimeUnit unit) {
         Objects.requireNonNull(callable, "callable");
         return Promise.start(this.entity).thenApplyDelayedSync(ignored -> {
            try {
               return callable.call();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }, delay, unit);
      }

      @Nonnull
      @Override
      public Promise<Void> runLater(@Nonnull Runnable runnable, long delayTicks) {
         Objects.requireNonNull(runnable, "runnable");
         return Promise.start(this.entity).thenRunDelayedSync(runnable, delayTicks);
      }

      @Nonnull
      @Override
      public Promise<Void> runLater(@Nonnull Runnable runnable, long delay, @Nonnull TimeUnit unit) {
         Objects.requireNonNull(runnable, "runnable");
         return Promise.start(this.entity).thenRunDelayedSync(runnable, delay, unit);
      }

      @Nonnull
      @Override
      public Task runRepeating(@Nonnull Consumer<Task> consumer, long delayTicks, long intervalTicks) {
         Objects.requireNonNull(consumer, "consumer");
         return new Schedulers.PaperTask(consumer, this.entity, delayTicks, intervalTicks);
      }

      @Nonnull
      @Override
      public Task runRepeating(@Nonnull Consumer<Task> consumer, long delay, @Nonnull TimeUnit delayUnit, long interval, @Nonnull TimeUnit intervalUnit) {
         Objects.requireNonNull(consumer, "consumer");
         long d = Ticks.from(delay, delayUnit);
         long i = Ticks.from(interval, intervalUnit);
         return new Schedulers.PaperTask(consumer, this.entity, d, i);
      }
   }

   private static final class PaperGlobalScheduler implements TaskScheduler {
      @Override
      public void execute(Runnable runnable) {
         Bukkit.getServer().getGlobalRegionScheduler().execute(LoaderUtils.getPlugin(), runnable);
      }

      @Nonnull
      @Override
      public ThreadContext getContext() {
         return ThreadContext.SYNC;
      }

      @Nonnull
      @Override
      public Task runRepeating(@Nonnull Consumer<Task> consumer, long delayTicks, long intervalTicks) {
         Objects.requireNonNull(consumer, "consumer");
         return new Schedulers.PaperTask(consumer, delayTicks, intervalTicks);
      }

      @Nonnull
      @Override
      public Task runRepeating(@Nonnull Consumer<Task> consumer, long delay, @Nonnull TimeUnit delayUnit, long interval, @Nonnull TimeUnit intervalUnit) {
         Objects.requireNonNull(consumer, "consumer");
         long d = Ticks.from(delay, delayUnit);
         long i = Ticks.from(interval, intervalUnit);
         return new Schedulers.PaperTask(consumer, d, i);
      }
   }

   private static final class PaperRegionScheduler implements TaskScheduler {
      private final Location location;

      private PaperRegionScheduler(Location location) {
         this.location = location;
      }

      @Override
      public void execute(Runnable runnable) {
         Bukkit.getServer().getRegionScheduler().execute(LoaderUtils.getPlugin(), this.location, runnable);
      }

      @Nonnull
      @Override
      public ThreadContext getContext() {
         return ThreadContext.SYNC;
      }

      @Nonnull
      @Override
      public <T> Promise<T> supply(@Nonnull Supplier<T> supplier) {
         Objects.requireNonNull(supplier, "supplier");
         return Promise.start(this.location).thenApplySync(ignored -> supplier.get());
      }

      @Nonnull
      @Override
      public <T> Promise<T> call(@Nonnull Callable<T> callable) {
         Objects.requireNonNull(callable, "callable");
         return Promise.start(this.location).thenApplySync(ignored -> {
            try {
               return callable.call();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         });
      }

      @Nonnull
      @Override
      public Promise<Void> run(@Nonnull Runnable runnable) {
         Objects.requireNonNull(runnable, "runnable");
         return Promise.start(this.location).thenRunSync(runnable);
      }

      @Nonnull
      @Override
      public <T> Promise<T> supplyLater(@Nonnull Supplier<T> supplier, long delayTicks) {
         Objects.requireNonNull(supplier, "supplier");
         return Promise.start(this.location).thenApplyDelayedSync(ignored -> supplier.get(), delayTicks);
      }

      @Nonnull
      @Override
      public <T> Promise<T> supplyLater(@Nonnull Supplier<T> supplier, long delay, @Nonnull TimeUnit unit) {
         Objects.requireNonNull(supplier, "supplier");
         return Promise.start(this.location).thenApplyDelayedSync(ignored -> supplier.get(), delay, unit);
      }

      @Nonnull
      @Override
      public <T> Promise<T> callLater(@Nonnull Callable<T> callable, long delayTicks) {
         Objects.requireNonNull(callable, "callable");
         return Promise.start(this.location).thenApplyDelayedSync(ignored -> {
            try {
               return callable.call();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }, delayTicks);
      }

      @Nonnull
      @Override
      public <T> Promise<T> callLater(@Nonnull Callable<T> callable, long delay, @Nonnull TimeUnit unit) {
         Objects.requireNonNull(callable, "callable");
         return Promise.start(this.location).thenApplyDelayedSync(ignored -> {
            try {
               return callable.call();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }, delay, unit);
      }

      @Nonnull
      @Override
      public Promise<Void> runLater(@Nonnull Runnable runnable, long delayTicks) {
         Objects.requireNonNull(runnable, "runnable");
         return Promise.start(this.location).thenRunDelayedSync(runnable, delayTicks);
      }

      @Nonnull
      @Override
      public Promise<Void> runLater(@Nonnull Runnable runnable, long delay, @Nonnull TimeUnit unit) {
         Objects.requireNonNull(runnable, "runnable");
         return Promise.start(this.location).thenRunDelayedSync(runnable, delay, unit);
      }

      @Nonnull
      @Override
      public Task runRepeating(@Nonnull Consumer<Task> consumer, long delayTicks, long intervalTicks) {
         Objects.requireNonNull(consumer, "consumer");
         return new Schedulers.PaperTask(consumer, this.location, delayTicks, intervalTicks);
      }

      @Nonnull
      @Override
      public Task runRepeating(@Nonnull Consumer<Task> consumer, long delay, @Nonnull TimeUnit delayUnit, long interval, @Nonnull TimeUnit intervalUnit) {
         Objects.requireNonNull(consumer, "consumer");
         long d = Ticks.from(delay, delayUnit);
         long i = Ticks.from(interval, intervalUnit);
         return new Schedulers.PaperTask(consumer, this.location, d, i);
      }
   }

   private static class PaperTask implements Task {
      private final Consumer<Task> backingTask;
      private final ScheduledTask paperTask;
      private final AtomicInteger counter = new AtomicInteger(0);
      private final AtomicBoolean cancelled = new AtomicBoolean(false);

      private PaperTask(Consumer<Task> backingTask, long delay, long interval) {
         if (delay <= 0L) {
            delay = 1L;
         }

         this.backingTask = backingTask;
         this.paperTask = Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(LoaderUtils.getPlugin(), a -> this.run(), delay, interval);
      }

      private PaperTask(Consumer<Task> backingTask, Entity entity, long delay, long interval) {
         if (delay <= 0L) {
            delay = 1L;
         }

         this.backingTask = backingTask;
         this.paperTask = entity.getScheduler().runAtFixedRate(LoaderUtils.getPlugin(), a -> this.run(), null, delay, interval);
      }

      private PaperTask(Consumer<Task> backingTask, Location location, long delay, long interval) {
         if (delay <= 0L) {
            delay = 1L;
         }

         this.backingTask = backingTask;
         this.paperTask = Bukkit.getServer().getRegionScheduler().runAtFixedRate(LoaderUtils.getPlugin(), location, a -> this.run(), delay, interval);
      }

      private void run() {
         if (this.cancelled.get()) {
            this.paperTask.cancel();
         } else {
            try {
               this.backingTask.accept(this);
               this.counter.incrementAndGet();
            } catch (Throwable e) {
               Log.severe("[SCHEDULER] Exception thrown whilst executing task");
               e.printStackTrace();
            }

            if (this.cancelled.get()) {
               this.paperTask.cancel();
            }
         }
      }

      @Override
      public int getTimesRan() {
         return this.counter.get();
      }

      @Override
      public boolean stop() {
         return !this.cancelled.getAndSet(true);
      }

      @Override
      public int getBukkitId() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasTerminated() {
         return this.cancelled.get();
      }
   }

   private static final class SyncScheduler implements TaskScheduler {
      @Override
      public void execute(Runnable runnable) {
         LumineExecutors.sync().execute(runnable);
      }

      @Nonnull
      @Override
      public ThreadContext getContext() {
         return ThreadContext.SYNC;
      }

      @Nonnull
      @Override
      public Task runRepeating(@Nonnull Consumer<Task> consumer, long delayTicks, long intervalTicks) {
         Objects.requireNonNull(consumer, "consumer");
         Schedulers.LumineTask task = new Schedulers.LumineTask(consumer);
         task.runTaskTimer(LoaderUtils.getPlugin(), delayTicks, intervalTicks);
         return task;
      }

      @Nonnull
      @Override
      public Task runRepeating(@Nonnull Consumer<Task> consumer, long delay, @Nonnull TimeUnit delayUnit, long interval, @Nonnull TimeUnit intervalUnit) {
         return this.runRepeating(consumer, Ticks.from(delay, delayUnit), Ticks.from(interval, intervalUnit));
      }
   }
}
