package io.lumine.mythic.bukkit.utils.tasks;

import io.lumine.mythic.bukkit.utils.Delegates;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.promise.ThreadContext;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public interface TaskScheduler extends Executor {
   @Nonnull
   ThreadContext getContext();

   @Nonnull
   default <T> Promise<T> supply(@Nonnull Supplier<T> supplier) {
      Objects.requireNonNull(supplier, "supplier");
      return Promise.supplying(this.getContext(), supplier);
   }

   @Nonnull
   default <T> Promise<T> call(@Nonnull Callable<T> callable) {
      Objects.requireNonNull(callable, "callable");
      return Promise.supplying(this.getContext(), Delegates.callableToSupplier(callable));
   }

   @Nonnull
   default Promise<Void> run(@Nonnull Runnable runnable) {
      Objects.requireNonNull(runnable, "runnable");
      return Promise.supplying(this.getContext(), Delegates.runnableToSupplier(runnable));
   }

   @Nonnull
   default <T> Promise<T> supplyLater(@Nonnull Supplier<T> supplier, long delayTicks) {
      Objects.requireNonNull(supplier, "supplier");
      return Promise.supplyingDelayed(this.getContext(), supplier, delayTicks);
   }

   @Nonnull
   default <T> Promise<T> supplyLater(@Nonnull Supplier<T> supplier, long delay, @Nonnull TimeUnit unit) {
      Objects.requireNonNull(supplier, "supplier");
      return Promise.supplyingDelayed(this.getContext(), supplier, delay, unit);
   }

   @Nonnull
   default <T> Promise<T> callLater(@Nonnull Callable<T> callable, long delayTicks) {
      Objects.requireNonNull(callable, "callable");
      return Promise.supplyingDelayed(this.getContext(), Delegates.callableToSupplier(callable), delayTicks);
   }

   @Nonnull
   default <T> Promise<T> callLater(@Nonnull Callable<T> callable, long delay, @Nonnull TimeUnit unit) {
      Objects.requireNonNull(callable, "callable");
      return Promise.supplyingDelayed(this.getContext(), Delegates.callableToSupplier(callable), delay, unit);
   }

   @Nonnull
   default Promise<Void> runLater(@Nonnull Runnable runnable, long delayTicks) {
      Objects.requireNonNull(runnable, "runnable");
      return Promise.supplyingDelayed(this.getContext(), Delegates.runnableToSupplier(runnable), delayTicks);
   }

   @Nonnull
   default Promise<Void> runLater(@Nonnull Runnable runnable, long delay, @Nonnull TimeUnit unit) {
      Objects.requireNonNull(runnable, "runnable");
      return Promise.supplyingDelayed(this.getContext(), Delegates.runnableToSupplier(runnable), delay, unit);
   }

   @Nonnull
   Task runRepeating(@Nonnull Consumer<Task> consumer, long value, long value2);

   @Nonnull
   Task runRepeating(@Nonnull Consumer<Task> consumer, long value, @Nonnull TimeUnit timeUnit, long value2, @Nonnull TimeUnit timeUnit2);

   @Nonnull
   default Task runRepeating(@Nonnull Runnable runnable, long delayTicks, long intervalTicks) {
      return this.runRepeating(Delegates.runnableToConsumer(runnable), delayTicks, intervalTicks);
   }

   @Nonnull
   default Task runRepeating(@Nonnull Runnable runnable, long delay, @Nonnull TimeUnit delayUnit, long interval, @Nonnull TimeUnit intervalUnit) {
      return this.runRepeating(Delegates.runnableToConsumer(runnable), delay, delayUnit, interval, intervalUnit);
   }
}
