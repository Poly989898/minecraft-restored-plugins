package io.lumine.mythic.bukkit.utils.promise;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import io.lumine.mythic.bukkit.utils.tasks.LumineExecutors;
import io.lumine.mythic.bukkit.utils.tasks.Ticks;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

final class LuminePromise<V> implements Promise<V> {
   private static final Consumer<Throwable> EXCEPTION_CONSUMER = throwable -> {
      Log.severe("[SCHEDULER] Exception thrown whilst executing task");
      throwable.printStackTrace();
   };
   @Nullable
   private Entity boundEntity = null;
   @Nullable
   private Location boundLocation = null;
   private final AtomicBoolean supplied = new AtomicBoolean(false);
   private final AtomicBoolean cancelled = new AtomicBoolean(false);
   @Nonnull
   private final CompletableFuture<V> fut;

   @Nonnull
   static <U> LuminePromise<U> empty() {
      return new LuminePromise();
   }

   @Nonnull
   static <U> LuminePromise<U> completed(@Nullable U value) {
      return new LuminePromise<>(value);
   }

   @Nonnull
   static <U> LuminePromise<U> exceptionally(@Nonnull Throwable t) {
      return new LuminePromise(t);
   }

   @Nonnull
   static <U> Promise<U> wrapFuture(@Nonnull Future<U> future) {
      if (future instanceof CompletableFuture) {
         return new LuminePromise(((CompletableFuture)future).thenApply(Function.identity()));
      } else if (future instanceof CompletionStage<?> fut) {
         return (Promise<U>)(new LuminePromise(fut.toCompletableFuture().thenApply(Function.identity())));
      } else if (future instanceof ListenableFuture<U> fut) {
         final LuminePromise<U> promise = empty();
         promise.supplied.set(true);
         Futures.addCallback(fut, new FutureCallback<U>() {
            public void onSuccess(@Nullable U result) {
               promise.complete(result);
            }

            public void onFailure(@Nonnull Throwable t) {
               promise.completeExceptionally(t);
            }
         }, LumineExecutors.asyncBukkit());
         return promise;
      } else if (future.isDone()) {
         try {
            return completed(future.get());
         } catch (ExecutionException e) {
            return exceptionally(e);
         } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
         }
      } else {
         return Promise.supplyingExceptionallyAsync(future::get);
      }
   }

   static Promise<Void> allOf(Promise<?>... promises) {
      return new LuminePromise<>(CompletableFuture.allOf(Arrays.stream(promises).map(Promise::toCompletableFuture).toArray(CompletableFuture[]::new)));
   }

   @Override
   public Promise<V> copy() {
      LuminePromise<V> copy = new LuminePromise<>(this.fut.thenApply(Function.identity()));
      copy.boundEntity = this.boundEntity;
      copy.boundLocation = this.boundLocation;
      return copy;
   }

   @Override
   public Promise<V> whenComplete(BiConsumer<? super V, ? super Throwable> action) {
      LuminePromise<V> observer = new LuminePromise<>(this.fut.whenComplete(action));
      observer.boundEntity = this.boundEntity;
      observer.boundLocation = this.boundLocation;
      return observer;
   }

   @Nonnull
   static <U> LuminePromise<U> completedAt(@Nonnull Entity entity, @Nullable U value) {
      LuminePromise<U> p = value == null ? new LuminePromise() : new LuminePromise<>(value);
      p.boundEntity = Objects.requireNonNull(entity, "entity");
      p.supplied.set(true);
      if (value != null) {
         p.complete(value);
      } else {
         p.complete(null);
      }

      return p;
   }

   @Nonnull
   static <U> LuminePromise<U> completedAt(@Nonnull Location location, @Nullable U value) {
      LuminePromise<U> p = value == null ? new LuminePromise() : new LuminePromise<>(value);
      p.boundLocation = Objects.requireNonNull(location, "location");
      p.supplied.set(true);
      if (value != null) {
         p.complete(value);
      } else {
         p.complete(null);
      }

      return p;
   }

   private LuminePromise() {
      this.fut = new CompletableFuture<>();
   }

   private LuminePromise(@Nullable V v) {
      this.fut = CompletableFuture.completedFuture(v);
      this.supplied.set(true);
   }

   private LuminePromise(@Nonnull Throwable t) {
      (this.fut = new CompletableFuture<>()).completeExceptionally(t);
      this.supplied.set(true);
   }

   private LuminePromise(@Nonnull CompletableFuture<V> fut) {
      this.fut = Objects.requireNonNull(fut, "future");
      this.supplied.set(true);
      this.cancelled.set(fut.isCancelled());
   }

   private void executeSync(@Nonnull Runnable runnable) {
      try {
         if (ServerVersion.isFolia()) {
            this.foliaExecute(runnable);
            return;
         }

         if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
            LumineExecutors.wrapRunnable(runnable).run();
         } else {
            LumineExecutors.sync().execute(runnable);
         }
      } catch (RuntimeException e) {
         if (!(runnable instanceof LuminePromise.ChainRunnable chain)) {
            throw e;
         }

         chain.completeExceptionally(e);
      }
   }

   private void executeAsync(@Nonnull Runnable runnable) {
      try {
         LumineExecutors.asyncLumine().execute(runnable);
      } catch (RuntimeException e) {
         if (!(runnable instanceof LuminePromise.ChainRunnable chain)) {
            throw e;
         }

         chain.completeExceptionally(e);
      }
   }

   private void executeDelayedSync(@Nonnull Runnable runnable, long delayTicks) {
      try {
         if (ServerVersion.isFolia()) {
            this.foliaExecuteDelayed(runnable, delayTicks);
            return;
         }

         if (delayTicks <= 0L) {
            this.executeSync(runnable);
         } else {
            Bukkit.getScheduler().runTaskLater(LoaderUtils.getPlugin(), LumineExecutors.wrapRunnable(runnable), delayTicks);
         }
      } catch (RuntimeException e) {
         if (!(runnable instanceof LuminePromise.ChainRunnable chain)) {
            throw e;
         }

         chain.completeExceptionally(e);
      }
   }

   private void executeDelayedAsync(@Nonnull Runnable runnable, long delayTicks) {
      try {
         if (delayTicks <= 0L) {
            this.executeAsync(runnable);
         } else if (ServerVersion.isFolia()) {
            LumineExecutors.asyncLumine().schedule(LumineExecutors.wrapRunnable(runnable), delayTicks * 50L, TimeUnit.MILLISECONDS);
         } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(LoaderUtils.getPlugin(), LumineExecutors.wrapRunnable(runnable), delayTicks);
         }
      } catch (RuntimeException e) {
         if (!(runnable instanceof LuminePromise.ChainRunnable chain)) {
            throw e;
         }

         chain.completeExceptionally(e);
      }
   }

   private void executeDelayedSync(@Nonnull Runnable runnable, long delay, TimeUnit unit) {
      try {
         if (ServerVersion.isFolia()) {
            this.foliaExecuteDelayed(runnable, Ticks.from(delay, unit));
            return;
         }

         if (delay <= 0L) {
            this.executeSync(runnable);
         } else {
            Bukkit.getScheduler().runTaskLater(LoaderUtils.getPlugin(), LumineExecutors.wrapRunnable(runnable), Ticks.from(delay, unit));
         }
      } catch (RuntimeException e) {
         if (!(runnable instanceof LuminePromise.ChainRunnable chain)) {
            throw e;
         }

         chain.completeExceptionally(e);
      }
   }

   private void executeDelayedAsync(@Nonnull Runnable runnable, long delay, TimeUnit unit) {
      try {
         if (delay <= 0L) {
            this.executeAsync(runnable);
         } else {
            LumineExecutors.asyncLumine().schedule(LumineExecutors.wrapRunnable(runnable), delay, unit);
         }
      } catch (RuntimeException e) {
         if (!(runnable instanceof LuminePromise.ChainRunnable chain)) {
            throw e;
         }

         chain.completeExceptionally(e);
      }
   }

   private void foliaExecute(@Nonnull Runnable runnable) {
      if (this.boundEntity != null) {
         if (this.boundEntity.getScheduler().run(LoaderUtils.getPlugin(), task -> runnable.run(), runnable) == null) {
            failRetired(runnable);
         }
      } else if (this.boundLocation != null) {
         Bukkit.getServer().getRegionScheduler().execute(LoaderUtils.getPlugin(), this.boundLocation, runnable);
      } else {
         Bukkit.getServer().getGlobalRegionScheduler().execute(LoaderUtils.getPlugin(), runnable);
      }
   }

   private void foliaExecuteDelayed(@Nonnull Runnable runnable, long delayTicks) {
      if (delayTicks <= 0L) {
         this.foliaExecute(runnable);
      } else if (this.boundEntity != null) {
         if (this.boundEntity.getScheduler().runDelayed(LoaderUtils.getPlugin(), task -> runnable.run(), runnable, delayTicks) == null) {
            failRetired(runnable);
         }
      } else if (this.boundLocation != null) {
         Bukkit.getServer().getRegionScheduler().runDelayed(LoaderUtils.getPlugin(), this.boundLocation, no -> runnable.run(), delayTicks);
      } else {
         Bukkit.getServer().getGlobalRegionScheduler().runDelayed(LoaderUtils.getPlugin(), no -> runnable.run(), delayTicks);
      }
   }

   private static void failRetired(Runnable runnable) {
      if (runnable instanceof LuminePromise.ChainRunnable chain) {
         chain.completeExceptionally(new PromiseRetiredException());
      } else {
         EXCEPTION_CONSUMER.accept(new PromiseRetiredException());
      }
   }

   private boolean complete(V value) {
      return !this.cancelled.get() && this.fut.complete(value);
   }

   private boolean completeExceptionally(@Nonnull Throwable t) {
      return !this.cancelled.get() && this.fut.completeExceptionally(t);
   }

   private void markAsSupplied() {
      if (!this.supplied.compareAndSet(false, true)) {
         throw new IllegalStateException("Promise is already being supplied.");
      }
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning) {
      boolean cancelled = this.fut.cancel(mayInterruptIfRunning);
      if (cancelled) {
         this.cancelled.set(true);
      }

      return cancelled;
   }

   @Override
   public boolean isCancelled() {
      return this.fut.isCancelled();
   }

   @Override
   public boolean isDone() {
      return this.fut.isDone();
   }

   @Override
   public V get() throws InterruptedException, ExecutionException {
      this.refuseTickThreadBlock();
      return (V)this.fut.get();
   }

   @Override
   public V get(long timeout, @Nonnull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return this.fut.get(timeout, unit);
   }

   @Override
   public V join() {
      this.refuseTickThreadBlock();
      return this.fut.join();
   }

   private void refuseTickThreadBlock() {
      if (!this.fut.isDone() && ServerVersion.isFolia() && Bukkit.isPrimaryThread()) {
         throw new TickThreadJoinException();
      }
   }

   @Override
   public V getNow(V valueIfAbsent) {
      return this.fut.getNow(valueIfAbsent);
   }

   @Override
   public CompletableFuture<V> toCompletableFuture() {
      return this.fut.thenApply(Function.identity());
   }

   @Override
   public void close() {
      this.cancel();
   }

   @Override
   public boolean hasTerminated() {
      return this.isCancelled();
   }

   @Nonnull
   @Override
   public Promise<V> supply(@Nullable V value) {
      this.markAsSupplied();
      this.complete(value);
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyException(@Nonnull Throwable exception) {
      this.markAsSupplied();
      this.completeExceptionally(exception);
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplySync(@Nonnull Supplier<V> supplier) {
      this.markAsSupplied();
      this.executeSync(new LuminePromise.SupplyRunnable(supplier));
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyAsync(@Nonnull Supplier<V> supplier) {
      this.markAsSupplied();
      this.executeAsync(new LuminePromise.SupplyRunnable(supplier));
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyDelayedSync(@Nonnull Supplier<V> supplier, long delayTicks) {
      this.markAsSupplied();
      this.executeDelayedSync(new LuminePromise.SupplyRunnable(supplier), delayTicks);
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyDelayedSync(@Nonnull Supplier<V> supplier, long delay, @Nonnull TimeUnit unit) {
      this.markAsSupplied();
      this.executeDelayedSync(new LuminePromise.SupplyRunnable(supplier), delay, unit);
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyDelayedAsync(@Nonnull Supplier<V> supplier, long delayTicks) {
      this.markAsSupplied();
      this.executeDelayedAsync(new LuminePromise.SupplyRunnable(supplier), delayTicks);
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyDelayedAsync(@Nonnull Supplier<V> supplier, long delay, @Nonnull TimeUnit unit) {
      this.markAsSupplied();
      this.executeDelayedAsync(new LuminePromise.SupplyRunnable(supplier), delay, unit);
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyExceptionallySync(@Nonnull Callable<V> callable) {
      this.markAsSupplied();
      this.executeSync(new LuminePromise.ThrowingSupplyRunnable(callable));
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyExceptionallyAsync(@Nonnull Callable<V> callable) {
      this.markAsSupplied();
      this.executeAsync(new LuminePromise.ThrowingSupplyRunnable(callable));
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyExceptionallyDelayedSync(@Nonnull Callable<V> callable, long delayTicks) {
      this.markAsSupplied();
      this.executeDelayedSync(new LuminePromise.ThrowingSupplyRunnable(callable), delayTicks);
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyExceptionallyDelayedSync(@Nonnull Callable<V> callable, long delay, @Nonnull TimeUnit unit) {
      this.markAsSupplied();
      this.executeDelayedSync(new LuminePromise.ThrowingSupplyRunnable(callable), delay, unit);
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyExceptionallyDelayedAsync(@Nonnull Callable<V> callable, long delayTicks) {
      this.markAsSupplied();
      this.executeDelayedAsync(new LuminePromise.ThrowingSupplyRunnable(callable), delayTicks);
      return this;
   }

   @Nonnull
   @Override
   public Promise<V> supplyExceptionallyDelayedAsync(@Nonnull Callable<V> callable, long delay, @Nonnull TimeUnit unit) {
      this.markAsSupplied();
      this.executeDelayedAsync(new LuminePromise.ThrowingSupplyRunnable(callable), delay, unit);
      return this;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenApplySync(@Nonnull Function<? super V, ? extends U> fn) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            promise.boundEntity = this.boundEntity;
            promise.boundLocation = this.boundLocation;
            this.executeSync(new ApplyRunnable<>(promise, fn, (V)value));
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenApplyAsync(@Nonnull Function<? super V, ? extends U> fn) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            this.executeAsync(new ApplyRunnable<>(promise, fn, (V)value));
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenApplyDelayedSync(@Nonnull Function<? super V, ? extends U> fn, long delayTicks) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            promise.boundEntity = this.boundEntity;
            promise.boundLocation = this.boundLocation;
            this.executeDelayedSync(new ApplyRunnable<>(promise, fn, (V)value), delayTicks);
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenApplyDelayedSync(@Nonnull Function<? super V, ? extends U> fn, long delay, @Nonnull TimeUnit unit) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            promise.boundEntity = this.boundEntity;
            promise.boundLocation = this.boundLocation;
            this.executeDelayedSync(new ApplyRunnable<>(promise, fn, (V)value), delay, unit);
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenApplyDelayedAsync(@Nonnull Function<? super V, ? extends U> fn, long delayTicks) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            this.executeDelayedAsync(new ApplyRunnable<>(promise, fn, (V)value), delayTicks);
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenApplyDelayedAsync(@Nonnull Function<? super V, ? extends U> fn, long delay, @Nonnull TimeUnit unit) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            this.executeDelayedAsync(new ApplyRunnable<>(promise, fn, (V)value), delay, unit);
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenComposeSync(@Nonnull Function<? super V, ? extends Promise<U>> fn) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            promise.boundEntity = this.boundEntity;
            promise.boundLocation = this.boundLocation;
            this.executeSync(new ComposeRunnable<>(promise, fn, (V)value, true));
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenComposeAsync(@Nonnull Function<? super V, ? extends Promise<U>> fn) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            this.executeAsync(new ComposeRunnable<>(promise, fn, (V)value, false));
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public Promise<V> exceptionallySync(@Nonnull Function<Throwable, ? extends V> fn) {
      LuminePromise<V> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t == null) {
            promise.complete((V)value);
         } else {
            promise.boundEntity = this.boundEntity;
            promise.boundLocation = this.boundLocation;
            this.executeSync(new ExceptionallyRunnable<>(promise, fn, t));
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public Promise<V> exceptionallyAsync(@Nonnull Function<Throwable, ? extends V> fn) {
      LuminePromise<V> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t == null) {
            promise.complete((V)value);
         } else {
            this.executeAsync(new ExceptionallyRunnable<>(promise, fn, t));
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public Promise<V> exceptionallyDelayedSync(@Nonnull Function<Throwable, ? extends V> fn, long delayTicks) {
      LuminePromise<V> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t == null) {
            promise.complete((V)value);
         } else {
            promise.boundEntity = this.boundEntity;
            promise.boundLocation = this.boundLocation;
            this.executeDelayedSync(new ExceptionallyRunnable<>(promise, fn, t), delayTicks);
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public Promise<V> exceptionallyDelayedSync(@Nonnull Function<Throwable, ? extends V> fn, long delay, @Nonnull TimeUnit unit) {
      LuminePromise<V> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t == null) {
            promise.complete((V)value);
         } else {
            promise.boundEntity = this.boundEntity;
            promise.boundLocation = this.boundLocation;
            this.executeDelayedSync(new ExceptionallyRunnable<>(promise, fn, t), delay, unit);
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public Promise<V> exceptionallyDelayedAsync(@Nonnull Function<Throwable, ? extends V> fn, long delayTicks) {
      LuminePromise<V> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t == null) {
            promise.complete((V)value);
         } else {
            this.executeDelayedAsync(new ExceptionallyRunnable<>(promise, fn, t), delayTicks);
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public Promise<V> exceptionallyDelayedAsync(@Nonnull Function<Throwable, ? extends V> fn, long delay, @Nonnull TimeUnit unit) {
      LuminePromise<V> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t == null) {
            promise.complete((V)value);
         } else {
            this.executeDelayedAsync(new ExceptionallyRunnable<>(promise, fn, t), delay, unit);
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenComposeDelayedSync(@Nonnull Function<? super V, ? extends Promise<U>> fn, long delayTicks) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            promise.boundEntity = this.boundEntity;
            promise.boundLocation = this.boundLocation;
            this.executeDelayedSync(new ComposeRunnable<>(promise, fn, (V)value, true), delayTicks);
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenComposeDelayedSync(@Nonnull Function<? super V, ? extends Promise<U>> fn, long delay, @Nonnull TimeUnit unit) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            promise.boundEntity = this.boundEntity;
            promise.boundLocation = this.boundLocation;
            this.executeDelayedSync(new ComposeRunnable<>(promise, fn, (V)value, true), delay, unit);
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenComposeDelayedAsync(@Nonnull Function<? super V, ? extends Promise<U>> fn, long delayTicks) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            this.executeDelayedAsync(new ComposeRunnable<>(promise, fn, (V)value, false), delayTicks);
         }
      });
      return promise;
   }

   @Nonnull
   @Override
   public <U> Promise<U> thenComposeDelayedAsync(@Nonnull Function<? super V, ? extends Promise<U>> fn, long delay, @Nonnull TimeUnit unit) {
      LuminePromise<U> promise = empty();
      this.fut.whenComplete((value, t) -> {
         if (t != null) {
            promise.completeExceptionally(t);
         } else {
            this.executeDelayedAsync(new ComposeRunnable<>(promise, fn, (V)value, false), delay, unit);
         }
      });
      return promise;
   }

   private final class ApplyRunnable<U> implements LuminePromise.ChainRunnable {
      private final LuminePromise<U> promise;
      private final Function<? super V, ? extends U> function;
      private final V value;

      private ApplyRunnable(LuminePromise<U> promise, Function<? super V, ? extends U> function, V value) {
         this.promise = promise;
         this.function = function;
         this.value = value;
      }

      @Override
      public void run() {
         if (!LuminePromise.this.cancelled.get()) {
            try {
               this.promise.complete((U)this.function.apply(this.value));
            } catch (Throwable e) {
               LuminePromise.EXCEPTION_CONSUMER.accept(e);
               this.promise.completeExceptionally(e);
            }
         }
      }

      @Override
      public void completeExceptionally(@Nonnull Throwable t) {
         this.promise.completeExceptionally(t);
      }
   }

   private interface ChainRunnable extends Runnable {
      void completeExceptionally(@Nonnull Throwable throwable);
   }

   private final class ComposeRunnable<U> implements LuminePromise.ChainRunnable {
      private final LuminePromise<U> promise;
      private final Function<? super V, ? extends Promise<U>> function;
      private final V value;
      private final boolean sync;

      private ComposeRunnable(LuminePromise<U> promise, Function<? super V, ? extends Promise<U>> function, V value, boolean sync) {
         this.promise = promise;
         this.function = function;
         this.value = value;
         this.sync = sync;
      }

      @Override
      public void run() {
         if (!LuminePromise.this.cancelled.get()) {
            try {
               Promise<U> p = (Promise<U>)this.function.apply(this.value);
               if (p == null) {
                  this.promise.complete(null);
               } else {
                  p.whenComplete((value, failure) -> {
                     if (failure == null) {
                        this.promise.complete((U)value);
                     } else {
                        this.promise.completeExceptionally(failure);
                     }
                  });
               }
            } catch (Throwable e) {
               LuminePromise.EXCEPTION_CONSUMER.accept(e);
               this.promise.completeExceptionally(e);
            }
         }
      }

      @Override
      public void completeExceptionally(@Nonnull Throwable t) {
         this.promise.completeExceptionally(t);
      }
   }

   private final class ExceptionallyRunnable<U> implements LuminePromise.ChainRunnable {
      private final LuminePromise<U> promise;
      private final Function<Throwable, ? extends U> function;
      private final Throwable t;

      private ExceptionallyRunnable(LuminePromise<U> promise, Function<Throwable, ? extends U> function, Throwable t) {
         this.promise = promise;
         this.function = function;
         this.t = t;
      }

      @Override
      public void run() {
         try {
            this.promise.complete((U)this.function.apply(this.t));
         } catch (Throwable e) {
            LuminePromise.EXCEPTION_CONSUMER.accept(e);
            this.promise.completeExceptionally(e);
         }
      }

      @Override
      public void completeExceptionally(@Nonnull Throwable t) {
         this.promise.completeExceptionally(t);
      }
   }

   private final class SupplyRunnable implements LuminePromise.ChainRunnable {
      private final Supplier<V> supplier;

      private SupplyRunnable(Supplier<V> supplier) {
         this.supplier = supplier;
      }

      @Override
      public void run() {
         if (!LuminePromise.this.cancelled.get()) {
            try {
               LuminePromise.this.fut.complete(this.supplier.get());
            } catch (Throwable e) {
               LuminePromise.EXCEPTION_CONSUMER.accept(e);
               LuminePromise.this.fut.completeExceptionally(e);
            }
         }
      }

      @Override
      public void completeExceptionally(@Nonnull Throwable t) {
         LuminePromise.this.completeExceptionally(t);
      }
   }

   private final class ThrowingSupplyRunnable implements LuminePromise.ChainRunnable {
      private final Callable<V> supplier;

      private ThrowingSupplyRunnable(Callable<V> supplier) {
         this.supplier = supplier;
      }

      @Override
      public void run() {
         if (!LuminePromise.this.cancelled.get()) {
            try {
               LuminePromise.this.fut.complete(this.supplier.call());
            } catch (Throwable e) {
               LuminePromise.EXCEPTION_CONSUMER.accept(e);
               LuminePromise.this.fut.completeExceptionally(e);
            }
         }
      }

      @Override
      public void completeExceptionally(@Nonnull Throwable t) {
         LuminePromise.this.completeExceptionally(t);
      }
   }
}
