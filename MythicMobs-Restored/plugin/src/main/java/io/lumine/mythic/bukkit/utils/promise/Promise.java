package io.lumine.mythic.bukkit.utils.promise;

import io.lumine.mythic.bukkit.utils.Delegates;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface Promise<V> extends Future<V>, Terminable {
   static Promise<Void> allOf(Promise<?>... promises) {
      return LuminePromise.allOf(promises);
   }

   Promise<V> copy();

   Promise<V> whenComplete(BiConsumer<? super V, ? super Throwable> biConsumer);

   @Nonnull
   static <U> Promise<U> empty() {
      return LuminePromise.empty();
   }

   @Nonnull
   static Promise<Void> start() {
      return LuminePromise.completed(null);
   }

   @Nonnull
   static Promise<Void> start(@Nonnull Entity entity) {
      return LuminePromise.completedAt(entity, null);
   }

   @Nonnull
   static Promise<Void> start(@Nonnull Location location) {
      return LuminePromise.completedAt(location, null);
   }

   @Nonnull
   static <U> Promise<U> completed(@Nullable U value) {
      return LuminePromise.completed(value);
   }

   @Nonnull
   static <U> Promise<U> exceptionally(@Nonnull Throwable exception) {
      return LuminePromise.exceptionally(exception);
   }

   @Nonnull
   static <U> Promise<U> wrapFuture(@Nonnull Future<U> future) {
      return LuminePromise.wrapFuture(future);
   }

   @Nonnull
   static <U> Promise<U> supplying(@Nonnull ThreadContext context, @Nonnull Supplier<U> supplier) {
      Promise<U> p = empty();
      return p.supply(context, supplier);
   }

   @Nonnull
   static <U> Promise<U> supplyingSync(@Nonnull Supplier<U> supplier) {
      Promise<U> p = empty();
      return p.supplySync(supplier);
   }

   @Nonnull
   static <U> Promise<U> supplyingAsync(@Nonnull Supplier<U> supplier) {
      Promise<U> p = empty();
      return p.supplyAsync(supplier);
   }

   @Nonnull
   static <U> Promise<U> supplyingDelayed(@Nonnull ThreadContext context, @Nonnull Supplier<U> supplier, long delayTicks) {
      Promise<U> p = empty();
      return p.supplyDelayed(context, supplier, delayTicks);
   }

   @Nonnull
   static <U> Promise<U> supplyingDelayed(@Nonnull ThreadContext context, @Nonnull Supplier<U> supplier, long delay, @Nonnull TimeUnit unit) {
      Promise<U> p = empty();
      return p.supplyDelayed(context, supplier, delay, unit);
   }

   @Nonnull
   static <U> Promise<U> supplyingDelayedSync(@Nonnull Supplier<U> supplier, long delayTicks) {
      Promise<U> p = empty();
      return p.supplyDelayedSync(supplier, delayTicks);
   }

   @Nonnull
   static <U> Promise<U> supplyingDelayedSync(@Nonnull Supplier<U> supplier, long delay, @Nonnull TimeUnit unit) {
      Promise<U> p = empty();
      return p.supplyDelayedSync(supplier, delay, unit);
   }

   @Nonnull
   static <U> Promise<U> supplyingDelayedAsync(@Nonnull Supplier<U> supplier, long delayTicks) {
      Promise<U> p = empty();
      return p.supplyDelayedAsync(supplier, delayTicks);
   }

   @Nonnull
   static <U> Promise<U> supplyingDelayedAsync(@Nonnull Supplier<U> supplier, long delay, @Nonnull TimeUnit unit) {
      Promise<U> p = empty();
      return p.supplyDelayedAsync(supplier, delay, unit);
   }

   @Nonnull
   static <U> Promise<U> supplyingExceptionally(@Nonnull ThreadContext context, @Nonnull Callable<U> callable) {
      Promise<U> p = empty();
      return p.supplyExceptionally(context, callable);
   }

   @Nonnull
   static <U> Promise<U> supplyingExceptionallySync(@Nonnull Callable<U> callable) {
      Promise<U> p = empty();
      return p.supplyExceptionallySync(callable);
   }

   @Nonnull
   static <U> Promise<U> supplyingExceptionallyAsync(@Nonnull Callable<U> callable) {
      Promise<U> p = empty();
      return p.supplyExceptionallyAsync(callable);
   }

   @Nonnull
   static <U> Promise<U> supplyingExceptionallyDelayed(@Nonnull ThreadContext context, @Nonnull Callable<U> callable, long delayTicks) {
      Promise<U> p = empty();
      return p.supplyExceptionallyDelayed(context, callable, delayTicks);
   }

   @Nonnull
   static <U> Promise<U> supplyingExceptionallyDelayed(@Nonnull ThreadContext context, @Nonnull Callable<U> callable, long delay, @Nonnull TimeUnit unit) {
      Promise<U> p = empty();
      return p.supplyExceptionallyDelayed(context, callable, delay, unit);
   }

   @Nonnull
   static <U> Promise<U> supplyingExceptionallyDelayedSync(@Nonnull Callable<U> callable, long delayTicks) {
      Promise<U> p = empty();
      return p.supplyExceptionallyDelayedSync(callable, delayTicks);
   }

   @Nonnull
   static <U> Promise<U> supplyingExceptionallyDelayedSync(@Nonnull Callable<U> callable, long delay, @Nonnull TimeUnit unit) {
      Promise<U> p = empty();
      return p.supplyExceptionallyDelayedSync(callable, delay, unit);
   }

   @Nonnull
   static <U> Promise<U> supplyingExceptionallyDelayedAsync(@Nonnull Callable<U> callable, long delayTicks) {
      Promise<U> p = empty();
      return p.supplyExceptionallyDelayedAsync(callable, delayTicks);
   }

   @Nonnull
   static <U> Promise<U> supplyingExceptionallyDelayedAsync(@Nonnull Callable<U> callable, long delay, @Nonnull TimeUnit unit) {
      Promise<U> p = empty();
      return p.supplyExceptionallyDelayedAsync(callable, delay, unit);
   }

   default boolean cancel() {
      return this.cancel(true);
   }

   V join();

   V getNow(V v);

   @Nonnull
   Promise<V> supply(@Nullable V v);

   @Nonnull
   Promise<V> supplyException(@Nonnull Throwable throwable);

   @Nonnull
   default Promise<V> supply(@Nonnull ThreadContext context, @Nonnull Supplier<V> supplier) {
      switch (context) {
         case SYNC:
            return this.supplySync(supplier);
         case ASYNC:
            return this.supplyAsync(supplier);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   Promise<V> supplySync(@Nonnull Supplier<V> supplier);

   @Nonnull
   Promise<V> supplyAsync(@Nonnull Supplier<V> supplier);

   @Nonnull
   default Promise<V> supplyDelayed(@Nonnull ThreadContext context, @Nonnull Supplier<V> supplier, long delayTicks) {
      switch (context) {
         case SYNC:
            return this.supplyDelayedSync(supplier, delayTicks);
         case ASYNC:
            return this.supplyDelayedAsync(supplier, delayTicks);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   default Promise<V> supplyDelayed(@Nonnull ThreadContext context, @Nonnull Supplier<V> supplier, long delay, @Nonnull TimeUnit unit) {
      switch (context) {
         case SYNC:
            return this.supplyDelayedSync(supplier, delay, unit);
         case ASYNC:
            return this.supplyDelayedAsync(supplier, delay, unit);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   Promise<V> supplyDelayedSync(@Nonnull Supplier<V> supplier, long value);

   @Nonnull
   Promise<V> supplyDelayedSync(@Nonnull Supplier<V> supplier, long value, @Nonnull TimeUnit timeUnit);

   @Nonnull
   Promise<V> supplyDelayedAsync(@Nonnull Supplier<V> supplier, long value);

   @Nonnull
   Promise<V> supplyDelayedAsync(@Nonnull Supplier<V> supplier, long value, @Nonnull TimeUnit timeUnit);

   @Nonnull
   default Promise<V> supplyExceptionally(@Nonnull ThreadContext context, @Nonnull Callable<V> callable) {
      switch (context) {
         case SYNC:
            return this.supplyExceptionallySync(callable);
         case ASYNC:
            return this.supplyExceptionallyAsync(callable);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   Promise<V> supplyExceptionallySync(@Nonnull Callable<V> callable);

   @Nonnull
   Promise<V> supplyExceptionallyAsync(@Nonnull Callable<V> callable);

   @Nonnull
   default Promise<V> supplyExceptionallyDelayed(@Nonnull ThreadContext context, @Nonnull Callable<V> callable, long delayTicks) {
      switch (context) {
         case SYNC:
            return this.supplyExceptionallyDelayedSync(callable, delayTicks);
         case ASYNC:
            return this.supplyExceptionallyDelayedAsync(callable, delayTicks);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   default Promise<V> supplyExceptionallyDelayed(@Nonnull ThreadContext context, @Nonnull Callable<V> callable, long delay, @Nonnull TimeUnit unit) {
      switch (context) {
         case SYNC:
            return this.supplyExceptionallyDelayedSync(callable, delay, unit);
         case ASYNC:
            return this.supplyExceptionallyDelayedAsync(callable, delay, unit);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   Promise<V> supplyExceptionallyDelayedSync(@Nonnull Callable<V> callable, long value);

   @Nonnull
   Promise<V> supplyExceptionallyDelayedSync(@Nonnull Callable<V> callable, long value, @Nonnull TimeUnit timeUnit);

   @Nonnull
   Promise<V> supplyExceptionallyDelayedAsync(@Nonnull Callable<V> callable, long value);

   @Nonnull
   Promise<V> supplyExceptionallyDelayedAsync(@Nonnull Callable<V> callable, long value, @Nonnull TimeUnit timeUnit);

   @Nonnull
   default <U> Promise<U> thenApply(@Nonnull ThreadContext context, @Nonnull Function<? super V, ? extends U> fn) {
      switch (context) {
         case SYNC:
            return this.thenApplySync(fn);
         case ASYNC:
            return this.thenApplyAsync(fn);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   <U> Promise<U> thenApplySync(@Nonnull Function<? super V, ? extends U> function);

   @Nonnull
   <U> Promise<U> thenApplyAsync(@Nonnull Function<? super V, ? extends U> function);

   @Nonnull
   default <U> Promise<U> thenApplyDelayed(@Nonnull ThreadContext context, @Nonnull Function<? super V, ? extends U> fn, long delayTicks) {
      switch (context) {
         case SYNC:
            return this.thenApplyDelayedSync(fn, delayTicks);
         case ASYNC:
            return this.thenApplyDelayedAsync(fn, delayTicks);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   default <U> Promise<U> thenApplyDelayed(@Nonnull ThreadContext context, @Nonnull Function<? super V, ? extends U> fn, long delay, @Nonnull TimeUnit unit) {
      switch (context) {
         case SYNC:
            return this.thenApplyDelayedSync(fn, delay, unit);
         case ASYNC:
            return this.thenApplyDelayedAsync(fn, delay, unit);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   <U> Promise<U> thenApplyDelayedSync(@Nonnull Function<? super V, ? extends U> function, long value);

   @Nonnull
   <U> Promise<U> thenApplyDelayedSync(@Nonnull Function<? super V, ? extends U> function, long value, @Nonnull TimeUnit timeUnit);

   @Nonnull
   <U> Promise<U> thenApplyDelayedAsync(@Nonnull Function<? super V, ? extends U> function, long value);

   @Nonnull
   <U> Promise<U> thenApplyDelayedAsync(@Nonnull Function<? super V, ? extends U> function, long value, @Nonnull TimeUnit timeUnit);

   @Nonnull
   default Promise<Void> thenAccept(@Nonnull ThreadContext context, @Nonnull Consumer<? super V> action) {
      switch (context) {
         case SYNC:
            return this.thenAcceptSync(action);
         case ASYNC:
            return this.thenAcceptAsync(action);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   default Promise<Void> thenAcceptSync(@Nonnull Consumer<? super V> action) {
      return this.thenApplySync(Delegates.consumerToFunction(action));
   }

   @Nonnull
   default Promise<Void> thenAcceptAsync(@Nonnull Consumer<? super V> action) {
      return this.thenApplyAsync(Delegates.consumerToFunction(action));
   }

   @Nonnull
   default Promise<Void> thenAcceptDelayed(@Nonnull ThreadContext context, @Nonnull Consumer<? super V> action, long delayTicks) {
      switch (context) {
         case SYNC:
            return this.thenAcceptDelayedSync(action, delayTicks);
         case ASYNC:
            return this.thenAcceptDelayedAsync(action, delayTicks);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   default Promise<Void> thenAcceptDelayed(@Nonnull ThreadContext context, @Nonnull Consumer<? super V> action, long delay, @Nonnull TimeUnit unit) {
      switch (context) {
         case SYNC:
            return this.thenAcceptDelayedSync(action, delay, unit);
         case ASYNC:
            return this.thenAcceptDelayedAsync(action, delay, unit);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   default Promise<Void> thenAcceptDelayedSync(@Nonnull Consumer<? super V> action, long delayTicks) {
      return this.thenApplyDelayedSync(Delegates.consumerToFunction(action), delayTicks);
   }

   @Nonnull
   default Promise<Void> thenAcceptDelayedSync(@Nonnull Consumer<? super V> action, long delay, @Nonnull TimeUnit unit) {
      return this.thenApplyDelayedSync(Delegates.consumerToFunction(action), delay, unit);
   }

   @Nonnull
   default Promise<Void> thenAcceptDelayedAsync(@Nonnull Consumer<? super V> action, long delayTicks) {
      return this.thenApplyDelayedAsync(Delegates.consumerToFunction(action), delayTicks);
   }

   @Nonnull
   default Promise<Void> thenAcceptDelayedAsync(@Nonnull Consumer<? super V> action, long delay, @Nonnull TimeUnit unit) {
      return this.thenApplyDelayedAsync(Delegates.consumerToFunction(action), delay, unit);
   }

   @Nonnull
   default Promise<Void> thenRun(@Nonnull ThreadContext context, @Nonnull Runnable action) {
      switch (context) {
         case SYNC:
            return this.thenRunSync(action);
         case ASYNC:
            return this.thenRunAsync(action);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   default Promise<Void> thenRunSync(@Nonnull Runnable action) {
      return this.thenApplySync(Delegates.runnableToFunction(action));
   }

   @Nonnull
   default Promise<Void> thenRunAsync(@Nonnull Runnable action) {
      return this.thenApplyAsync(Delegates.runnableToFunction(action));
   }

   @Nonnull
   default Promise<Void> thenRunDelayed(@Nonnull ThreadContext context, @Nonnull Runnable action, long delayTicks) {
      switch (context) {
         case SYNC:
            return this.thenRunDelayedSync(action, delayTicks);
         case ASYNC:
            return this.thenRunDelayedAsync(action, delayTicks);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   default Promise<Void> thenRunDelayed(@Nonnull ThreadContext context, @Nonnull Runnable action, long delay, @Nonnull TimeUnit unit) {
      switch (context) {
         case SYNC:
            return this.thenRunDelayedSync(action, delay, unit);
         case ASYNC:
            return this.thenRunDelayedAsync(action, delay, unit);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   default Promise<Void> thenRunDelayedSync(@Nonnull Runnable action, long delayTicks) {
      return this.thenApplyDelayedSync(Delegates.runnableToFunction(action), delayTicks);
   }

   @Nonnull
   default Promise<Void> thenRunDelayedSync(@Nonnull Runnable action, long delay, @Nonnull TimeUnit unit) {
      return this.thenApplyDelayedSync(Delegates.runnableToFunction(action), delay, unit);
   }

   @Nonnull
   default Promise<Void> thenRunDelayedAsync(@Nonnull Runnable action, long delayTicks) {
      return this.thenApplyDelayedAsync(Delegates.runnableToFunction(action), delayTicks);
   }

   @Nonnull
   default Promise<Void> thenRunDelayedAsync(@Nonnull Runnable action, long delay, @Nonnull TimeUnit unit) {
      return this.thenApplyDelayedAsync(Delegates.runnableToFunction(action), delay, unit);
   }

   @Nonnull
   default <U> Promise<U> thenCompose(@Nonnull ThreadContext context, @Nonnull Function<? super V, ? extends Promise<U>> fn) {
      switch (context) {
         case SYNC:
            return this.thenComposeSync(fn);
         case ASYNC:
            return this.thenComposeAsync(fn);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   <U> Promise<U> thenComposeSync(@Nonnull Function<? super V, ? extends Promise<U>> function);

   @Nonnull
   <U> Promise<U> thenComposeAsync(@Nonnull Function<? super V, ? extends Promise<U>> function);

   @Nonnull
   default <U> Promise<U> thenComposeDelayedSync(@Nonnull ThreadContext context, @Nonnull Function<? super V, ? extends Promise<U>> fn, long delayTicks) {
      switch (context) {
         case SYNC:
            return this.thenComposeDelayedSync(fn, delayTicks);
         case ASYNC:
            return this.thenComposeDelayedAsync(fn, delayTicks);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   default <U> Promise<U> thenComposeDelayedSync(
      @Nonnull ThreadContext context, @Nonnull Function<? super V, ? extends Promise<U>> fn, long delay, @Nonnull TimeUnit unit
   ) {
      switch (context) {
         case SYNC:
            return this.thenComposeDelayedSync(fn, delay, unit);
         case ASYNC:
            return this.thenComposeDelayedAsync(fn, delay, unit);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   <U> Promise<U> thenComposeDelayedSync(@Nonnull Function<? super V, ? extends Promise<U>> function, long value);

   @Nonnull
   <U> Promise<U> thenComposeDelayedSync(@Nonnull Function<? super V, ? extends Promise<U>> function, long value, @Nonnull TimeUnit timeUnit);

   @Nonnull
   <U> Promise<U> thenComposeDelayedAsync(@Nonnull Function<? super V, ? extends Promise<U>> function, long value);

   @Nonnull
   <U> Promise<U> thenComposeDelayedAsync(@Nonnull Function<? super V, ? extends Promise<U>> function, long value, @Nonnull TimeUnit timeUnit);

   @Nonnull
   default Promise<V> exceptionally(@Nonnull ThreadContext context, @Nonnull Function<Throwable, ? extends V> fn) {
      switch (context) {
         case SYNC:
            return this.exceptionallySync(fn);
         case ASYNC:
            return this.exceptionallySync(fn);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   Promise<V> exceptionallySync(@Nonnull Function<Throwable, ? extends V> function);

   @Nonnull
   Promise<V> exceptionallyAsync(@Nonnull Function<Throwable, ? extends V> function);

   @Nonnull
   default Promise<V> exceptionallyDelayed(@Nonnull ThreadContext context, @Nonnull Function<Throwable, ? extends V> fn, long delayTicks) {
      switch (context) {
         case SYNC:
            return this.exceptionallyDelayedSync(fn, delayTicks);
         case ASYNC:
            return this.exceptionallyDelayedAsync(fn, delayTicks);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   default Promise<V> exceptionallyDelayed(@Nonnull ThreadContext context, @Nonnull Function<Throwable, ? extends V> fn, long delay, @Nonnull TimeUnit unit) {
      switch (context) {
         case SYNC:
            return this.exceptionallyDelayedSync(fn, delay, unit);
         case ASYNC:
            return this.exceptionallyDelayedAsync(fn, delay, unit);
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   Promise<V> exceptionallyDelayedSync(@Nonnull Function<Throwable, ? extends V> function, long value);

   @Nonnull
   Promise<V> exceptionallyDelayedSync(@Nonnull Function<Throwable, ? extends V> function, long value, @Nonnull TimeUnit timeUnit);

   @Nonnull
   Promise<V> exceptionallyDelayedAsync(@Nonnull Function<Throwable, ? extends V> function, long value);

   @Nonnull
   Promise<V> exceptionallyDelayedAsync(@Nonnull Function<Throwable, ? extends V> function, long value, @Nonnull TimeUnit timeUnit);

   CompletableFuture<V> toCompletableFuture();
}
