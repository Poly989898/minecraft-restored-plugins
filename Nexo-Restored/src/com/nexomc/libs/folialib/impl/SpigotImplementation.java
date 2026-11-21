package com.nexomc.libs.folialib.impl;

import com.nexomc.libs.folialib.FoliaLib;
import com.nexomc.libs.folialib.enums.EntityTaskResult;
import com.nexomc.libs.folialib.type.Ref;
import com.nexomc.libs.folialib.util.FoliaLibOptions;
import com.nexomc.libs.folialib.util.TimeConverter;
import com.nexomc.libs.folialib.wrapper.task.WrappedBukkitTask;
import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpigotImplementation implements PlatformScheduler {
   private final FoliaLib foliaLib;
   private final FoliaLibOptions options;
   private final Plugin plugin;
   private final @NotNull BukkitScheduler scheduler;

   public SpigotImplementation(FoliaLib foliaLib) {
      this.foliaLib = foliaLib;
      this.options = foliaLib.getOptions();
      this.plugin = foliaLib.getPlugin();
      this.scheduler = this.plugin.getServer().getScheduler();
   }

   public boolean isOwnedByCurrentRegion(@NotNull Location location) {
      return this.plugin.getServer().isPrimaryThread();
   }

   public boolean isOwnedByCurrentRegion(@NotNull Location location, int squareRadiusChunks) {
      return this.plugin.getServer().isPrimaryThread();
   }

   public boolean isOwnedByCurrentRegion(@NotNull Block block) {
      return this.plugin.getServer().isPrimaryThread();
   }

   public boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ) {
      return this.plugin.getServer().isPrimaryThread();
   }

   public boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ, int squareRadiusChunks) {
      return this.plugin.getServer().isPrimaryThread();
   }

   public boolean isOwnedByCurrentRegion(@NotNull Entity entity) {
      return this.plugin.getServer().isPrimaryThread();
   }

   public boolean isGlobalTickThread() {
      return this.plugin.getServer().isPrimaryThread();
   }

   public @NotNull CompletableFuture<Void> runNextTick(@NotNull Consumer<WrappedTask> consumer) {
      CompletableFuture<Void> future = new CompletableFuture();
      this.scheduler.runTask(this.plugin, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete((Object)null);
      });
      return future;
   }

   public @NotNull CompletableFuture<Void> runAsync(@NotNull Consumer<WrappedTask> consumer) {
      CompletableFuture<Void> future = new CompletableFuture();
      this.scheduler.runTaskAsynchronously(this.plugin, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete((Object)null);
      });
      return future;
   }

   public WrappedTask runLater(@NotNull Runnable runnable, long delay) {
      return this.wrapTask(this.scheduler.runTaskLater(this.plugin, runnable, delay));
   }

   public @NotNull CompletableFuture<Void> runLater(@NotNull Consumer<WrappedTask> consumer, long delay) {
      CompletableFuture<Void> future = new CompletableFuture();
      this.scheduler.runTaskLater(this.plugin, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete((Object)null);
      }, delay);
      return future;
   }

   public WrappedTask runLater(@NotNull Runnable runnable, long delay, TimeUnit unit) {
      return this.runLater(runnable, TimeConverter.toTicks(delay, unit));
   }

   public @NotNull CompletableFuture<Void> runLater(@NotNull Consumer<WrappedTask> consumer, long delay, TimeUnit unit) {
      return this.runLater(consumer, TimeConverter.toTicks(delay, unit));
   }

   public WrappedTask runLaterAsync(@NotNull Runnable runnable, long delay) {
      return this.wrapTask(this.scheduler.runTaskLaterAsynchronously(this.plugin, runnable, delay));
   }

   public @NotNull CompletableFuture<Void> runLaterAsync(@NotNull Consumer<WrappedTask> consumer, long delay) {
      CompletableFuture<Void> future = new CompletableFuture();
      this.scheduler.runTaskLaterAsynchronously(this.plugin, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete((Object)null);
      }, delay);
      return future;
   }

   public WrappedTask runLaterAsync(@NotNull Runnable runnable, long delay, TimeUnit unit) {
      return this.runLaterAsync(runnable, TimeConverter.toTicks(delay, unit));
   }

   public @NotNull CompletableFuture<Void> runLaterAsync(@NotNull Consumer<WrappedTask> consumer, long delay, TimeUnit unit) {
      return this.runLaterAsync(consumer, TimeConverter.toTicks(delay, unit));
   }

   public WrappedTask runTimer(@NotNull Runnable runnable, long delay, long period) {
      return this.wrapTask(this.scheduler.runTaskTimer(this.plugin, runnable, delay, period));
   }

   public void runTimer(@NotNull Consumer<WrappedTask> consumer, long delay, long period) {
      this.scheduler.runTaskTimer(this.plugin, (task) -> consumer.accept(this.wrapTask(task)), delay, period);
   }

   public WrappedTask runTimer(@NotNull Runnable runnable, long delay, long period, TimeUnit unit) {
      return this.runTimer(runnable, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public void runTimer(@NotNull Consumer<WrappedTask> consumer, long delay, long period, TimeUnit unit) {
      this.runTimer(consumer, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public WrappedTask runTimerAsync(@NotNull Runnable runnable, long delay, long period) {
      return this.wrapTask(this.scheduler.runTaskTimerAsynchronously(this.plugin, runnable, delay, period));
   }

   public void runTimerAsync(@NotNull Consumer<WrappedTask> consumer, long delay, long period) {
      this.scheduler.runTaskTimerAsynchronously(this.plugin, (task) -> consumer.accept(this.wrapTask(task)), delay, period);
   }

   public WrappedTask runTimerAsync(@NotNull Runnable runnable, long delay, long period, TimeUnit unit) {
      return this.runTimerAsync(runnable, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public void runTimerAsync(@NotNull Consumer<WrappedTask> consumer, long delay, long period, TimeUnit unit) {
      this.runTimerAsync(consumer, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public @NotNull CompletableFuture<Void> runAtLocation(Location location, @NotNull Consumer<WrappedTask> consumer) {
      return this.runNextTick(consumer);
   }

   public WrappedTask runAtLocationLater(Location location, @NotNull Runnable runnable, long delay) {
      return this.runLater(runnable, delay);
   }

   public @NotNull CompletableFuture<Void> runAtLocationLater(Location location, @NotNull Consumer<WrappedTask> consumer, long delay) {
      return this.runLater(consumer, delay);
   }

   public WrappedTask runAtLocationLater(Location location, @NotNull Runnable runnable, long delay, TimeUnit unit) {
      return this.runAtLocationLater(location, runnable, TimeConverter.toTicks(delay, unit));
   }

   public @NotNull CompletableFuture<Void> runAtLocationLater(Location location, @NotNull Consumer<WrappedTask> consumer, long delay, TimeUnit unit) {
      return this.runAtLocationLater(location, consumer, TimeConverter.toTicks(delay, unit));
   }

   public WrappedTask runAtLocationTimer(Location location, @NotNull Runnable runnable, long delay, long period) {
      return this.runTimer(runnable, delay, period);
   }

   public void runAtLocationTimer(Location location, @NotNull Consumer<WrappedTask> consumer, long delay, long period) {
      this.runTimer(consumer, delay, period);
   }

   public WrappedTask runAtLocationTimer(Location location, @NotNull Runnable runnable, long delay, long period, TimeUnit unit) {
      return this.runAtLocationTimer(location, runnable, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public void runAtLocationTimer(Location location, @NotNull Consumer<WrappedTask> consumer, long delay, long period, TimeUnit unit) {
      this.runAtLocationTimer(location, consumer, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public @NotNull CompletableFuture<EntityTaskResult> runAtEntity(Entity entity, @NotNull Consumer<WrappedTask> consumer) {
      CompletableFuture<EntityTaskResult> future = new CompletableFuture();
      this.runNextTick((task) -> {
         consumer.accept(task);
         future.complete(EntityTaskResult.SUCCESS);
      });
      return future;
   }

   public @NotNull CompletableFuture<EntityTaskResult> runAtEntityWithFallback(Entity entity, @NotNull Consumer<WrappedTask> consumer, Runnable fallback) {
      CompletableFuture<EntityTaskResult> future = new CompletableFuture();
      this.runNextTick((task) -> {
         if (this.isValid(entity)) {
            consumer.accept(task);
            future.complete(EntityTaskResult.SUCCESS);
         } else {
            fallback.run();
            future.complete(EntityTaskResult.ENTITY_RETIRED);
         }

      });
      return future;
   }

   public WrappedTask runAtEntityLater(Entity entity, @NotNull Runnable runnable, long delay) {
      return this.runAtEntityLater(entity, runnable, (Runnable)null, delay);
   }

   public WrappedTask runAtEntityLater(Entity entity, @NotNull Runnable runnable, Runnable fallback, long delay) {
      if (!this.isValid(entity)) {
         if (fallback != null) {
            fallback.run();
         }

         return null;
      } else {
         return this.runLater(runnable, delay);
      }
   }

   public @NotNull CompletableFuture<Void> runAtEntityLater(Entity entity, @NotNull Consumer<WrappedTask> consumer, long delay) {
      return this.runAtEntityLater(entity, (Consumer)consumer, (Runnable)null, delay);
   }

   public @NotNull CompletableFuture<Void> runAtEntityLater(Entity entity, @NotNull Consumer<WrappedTask> consumer, @Nullable Runnable fallback, long delay) {
      CompletableFuture<Void> future = new CompletableFuture();
      if (!this.isValid(entity)) {
         future.complete((Object)null);
         if (fallback != null) {
            fallback.run();
         }

         return future;
      } else {
         this.runLater((Consumer)((task) -> {
            if (!this.isValid(entity)) {
               if (fallback != null) {
                  fallback.run();
               }

               future.complete((Object)null);
            } else {
               consumer.accept(task);
               future.complete((Object)null);
            }
         }), delay);
         return future;
      }
   }

   public WrappedTask runAtEntityLater(Entity entity, @NotNull Runnable runnable, long delay, TimeUnit unit) {
      return this.runAtEntityLater(entity, runnable, TimeConverter.toTicks(delay, unit));
   }

   public @NotNull CompletableFuture<Void> runAtEntityLater(Entity entity, @NotNull Consumer<WrappedTask> consumer, long delay, TimeUnit unit) {
      return this.runAtEntityLater(entity, consumer, TimeConverter.toTicks(delay, unit));
   }

   public WrappedTask runAtEntityTimer(Entity entity, @NotNull Runnable runnable, long delay, long period) {
      return this.runAtEntityTimer(entity, runnable, (Runnable)null, delay, period);
   }

   public WrappedTask runAtEntityTimer(Entity entity, @NotNull Runnable runnable, @Nullable Runnable fallback, long delay, long period) {
      if (!this.isValid(entity)) {
         if (fallback != null) {
            fallback.run();
         }

         return null;
      } else {
         Ref<WrappedTask> wtRef = new Ref<WrappedTask>();
         WrappedTask task = this.runTimer((Runnable)(() -> {
            WrappedTask wt = wtRef.get();
            if (wt != null && !this.isValid(entity)) {
               wt.cancel();
               if (fallback != null) {
                  fallback.run();
               }

            } else {
               runnable.run();
            }
         }), delay, period);
         wtRef.set(task);
         return task;
      }
   }

   public void runAtEntityTimer(Entity entity, @NotNull Consumer<WrappedTask> consumer, long delay, long period) {
      this.runAtEntityTimer(entity, (Consumer)consumer, (Runnable)null, delay, period);
   }

   public void runAtEntityTimer(Entity entity, @NotNull Consumer<WrappedTask> consumer, Runnable fallback, long delay, long period) {
      if (!this.isValid(entity)) {
         if (fallback != null) {
            fallback.run();
         }

      } else {
         this.runTimer((Consumer)((task) -> {
            if (!this.isValid(entity)) {
               if (fallback != null) {
                  fallback.run();
               }

            } else {
               consumer.accept(task);
            }
         }), delay, period);
      }
   }

   public WrappedTask runAtEntityTimer(Entity entity, @NotNull Runnable runnable, long delay, long period, TimeUnit unit) {
      return this.runAtEntityTimer(entity, runnable, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public void runAtEntityTimer(Entity entity, @NotNull Consumer<WrappedTask> consumer, long delay, long period, TimeUnit unit) {
      this.runAtEntityTimer(entity, consumer, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public void cancelTask(WrappedTask task) {
      task.cancel();
   }

   public void cancelAllTasks() {
      this.scheduler.cancelTasks(this.plugin);
   }

   public List<WrappedTask> getAllTasks() {
      return (List)this.scheduler.getPendingTasks().stream().filter((task) -> task.getOwner().equals(this.plugin)).map(this::wrapTask).collect(Collectors.toList());
   }

   public List<WrappedTask> getAllServerTasks() {
      return (List)this.scheduler.getPendingTasks().stream().map(this::wrapTask).collect(Collectors.toList());
   }

   public Player getPlayer(String name) {
      return this.getPlayerFromMainThread(() -> this.plugin.getServer().getPlayer(name));
   }

   public Player getPlayerExact(String name) {
      return this.getPlayerFromMainThread(() -> this.plugin.getServer().getPlayerExact(name));
   }

   public Player getPlayer(UUID uuid) {
      return this.getPlayerFromMainThread(() -> this.plugin.getServer().getPlayer(uuid));
   }

   private Player getPlayerFromMainThread(Supplier<Player> playerSupplier) {
      if (this.plugin.getServer().isPrimaryThread()) {
         return (Player)playerSupplier.get();
      } else {
         try {
            BukkitScheduler var10000 = this.scheduler;
            Plugin var10001 = this.plugin;
            Objects.requireNonNull(playerSupplier);
            return (Player)var10000.callSyncMethod(var10001, playerSupplier::get).get();
         } catch (ExecutionException | InterruptedException e) {
            ((Exception)e).printStackTrace();
            return null;
         }
      }
   }

   public CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
      return this.teleportAsync(entity, location, TeleportCause.PLUGIN);
   }

   public CompletableFuture<Boolean> teleportAsync(Entity entity, Location location, PlayerTeleportEvent.TeleportCause cause) {
      CompletableFuture<Boolean> future = new CompletableFuture();
      this.runAtEntity(entity, (task) -> {
         if (this.isValid(entity)) {
            boolean result = entity.teleport(location);
            future.complete(result);
         } else {
            future.complete(false);
         }

      });
      return future;
   }

   public WrappedTask wrapTask(@NotNull Object nativeTask) {
      Objects.requireNonNull(nativeTask, "nativeTask cannot be null");
      if (!(nativeTask instanceof BukkitTask)) {
         throw new IllegalArgumentException("The nativeTask provided must be a BukkitTask. Got: " + nativeTask.getClass().getName() + " instead.");
      } else {
         return new WrappedBukkitTask((BukkitTask)nativeTask);
      }
   }

   private boolean isValid(Entity entity) {
      boolean skipValidCheck = !this.options.useIsValidOnNonFolia();
      if (skipValidCheck) {
         return true;
      } else if (entity.isValid()) {
         return !(entity instanceof Player) || ((Player)entity).isOnline();
      } else {
         return entity instanceof Projectile && !entity.isDead();
      }
   }
}
