package com.nexomc.libs.folialib.impl;

import com.nexomc.libs.folialib.FoliaLib;
import com.nexomc.libs.folialib.enums.EntityTaskResult;
import com.nexomc.libs.folialib.util.InvalidTickDelayNotifier;
import com.nexomc.libs.folialib.util.TimeConverter;
import com.nexomc.libs.folialib.wrapper.task.WrappedFoliaTask;
import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class FoliaImplementation implements PlatformScheduler {
   private final FoliaLib foliaLib;
   private final Plugin plugin;
   private final GlobalRegionScheduler globalRegionScheduler;
   private final RegionScheduler regionScheduler;
   private final AsyncScheduler asyncScheduler;
   private final InvalidTickDelayNotifier tickNotifier;

   public FoliaImplementation(FoliaLib foliaLib) {
      this.foliaLib = foliaLib;
      this.plugin = foliaLib.getPlugin();
      this.globalRegionScheduler = this.plugin.getServer().getGlobalRegionScheduler();
      this.regionScheduler = this.plugin.getServer().getRegionScheduler();
      this.asyncScheduler = this.plugin.getServer().getAsyncScheduler();
      this.tickNotifier = this.foliaLib.getInvalidTickDelayNotifier();
   }

   public boolean isOwnedByCurrentRegion(@NotNull Location location) {
      return this.plugin.getServer().isOwnedByCurrentRegion(location);
   }

   public boolean isOwnedByCurrentRegion(@NotNull Location location, int squareRadiusChunks) {
      return this.plugin.getServer().isOwnedByCurrentRegion(location, squareRadiusChunks);
   }

   public boolean isOwnedByCurrentRegion(@NotNull Block block) {
      return this.plugin.getServer().isOwnedByCurrentRegion(block);
   }

   public boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ) {
      return this.plugin.getServer().isOwnedByCurrentRegion(world, chunkX, chunkZ);
   }

   public boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ, int squareRadiusChunks) {
      return this.plugin.getServer().isOwnedByCurrentRegion(world, chunkX, chunkZ, squareRadiusChunks);
   }

   public boolean isOwnedByCurrentRegion(@NotNull Entity entity) {
      return this.plugin.getServer().isOwnedByCurrentRegion(entity);
   }

   public boolean isGlobalTickThread() {
      return this.plugin.getServer().isGlobalTickThread();
   }

   public @NotNull CompletableFuture<Void> runNextTick(@NotNull Consumer<WrappedTask> consumer) {
      CompletableFuture<Void> future = new CompletableFuture();
      this.globalRegionScheduler.run(this.plugin, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete(null);
      });
      return future;
   }

   public @NotNull CompletableFuture<Void> runAsync(@NotNull Consumer<WrappedTask> consumer) {
      CompletableFuture<Void> future = new CompletableFuture();
      this.asyncScheduler.runNow(this.plugin, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete(null);
      });
      return future;
   }

   public WrappedTask runLater(@NotNull Runnable runnable, long delay) {
      delay = this.ensureValidDuration(delay);
      return this.wrapTask(this.globalRegionScheduler.runDelayed(this.plugin, (task) -> runnable.run(), delay));
   }

   public @NotNull CompletableFuture<Void> runLater(@NotNull Consumer<WrappedTask> consumer, long delay) {
      CompletableFuture<Void> future = new CompletableFuture();
      delay = this.ensureValidDuration(delay);
      this.globalRegionScheduler.runDelayed(this.plugin, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete(null);
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
      return this.runLaterAsync(runnable, TimeConverter.toMillis(delay), TimeUnit.MILLISECONDS);
   }

   public @NotNull CompletableFuture<Void> runLaterAsync(@NotNull Consumer<WrappedTask> consumer, long delay) {
      return this.runLaterAsync(consumer, TimeConverter.toMillis(delay), TimeUnit.MILLISECONDS);
   }

   public WrappedTask runLaterAsync(@NotNull Runnable runnable, long delay, TimeUnit unit) {
      return this.wrapTask(this.asyncScheduler.runDelayed(this.plugin, (task) -> runnable.run(), delay, unit));
   }

   public @NotNull CompletableFuture<Void> runLaterAsync(@NotNull Consumer<WrappedTask> consumer, long delay, TimeUnit unit) {
      CompletableFuture<Void> future = new CompletableFuture();
      this.asyncScheduler.runDelayed(this.plugin, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete(null);
      }, delay, unit);
      return future;
   }

   public WrappedTask runTimer(@NotNull Runnable runnable, long delay, long period) {
      delay = this.ensureValidDuration(delay);
      period = this.ensureValidDuration(period);
      return this.wrapTask(this.globalRegionScheduler.runAtFixedRate(this.plugin, (task) -> runnable.run(), delay, period));
   }

   public void runTimer(@NotNull Consumer<WrappedTask> consumer, long delay, long period) {
      delay = this.ensureValidDuration(delay);
      period = this.ensureValidDuration(period);
      this.globalRegionScheduler.runAtFixedRate(this.plugin, (task) -> consumer.accept(this.wrapTask(task)), delay, period);
   }

   public WrappedTask runTimer(@NotNull Runnable runnable, long delay, long period, TimeUnit unit) {
      return this.runTimer(runnable, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public void runTimer(@NotNull Consumer<WrappedTask> consumer, long delay, long period, TimeUnit unit) {
      this.runTimer(consumer, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public WrappedTask runTimerAsync(@NotNull Runnable runnable, long delay, long period) {
      return this.runTimerAsync(runnable, TimeConverter.toMillis(delay), TimeConverter.toMillis(period), TimeUnit.MILLISECONDS);
   }

   public void runTimerAsync(@NotNull Consumer<WrappedTask> consumer, long delay, long period) {
      this.runTimerAsync(consumer, TimeConverter.toMillis(delay), TimeConverter.toMillis(period), TimeUnit.MILLISECONDS);
   }

   public WrappedTask runTimerAsync(@NotNull Runnable runnable, long delay, long period, TimeUnit unit) {
      return this.wrapTask(this.asyncScheduler.runAtFixedRate(this.plugin, (task) -> runnable.run(), delay, period, unit));
   }

   public void runTimerAsync(@NotNull Consumer<WrappedTask> consumer, long delay, long period, TimeUnit unit) {
      this.asyncScheduler.runAtFixedRate(this.plugin, (task) -> consumer.accept(this.wrapTask(task)), delay, period, unit);
   }

   public @NotNull CompletableFuture<Void> runAtLocation(Location location, @NotNull Consumer<WrappedTask> consumer) {
      CompletableFuture<Void> future = new CompletableFuture();
      this.regionScheduler.run(this.plugin, location, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete(null);
      });
      return future;
   }

   public WrappedTask runAtLocationLater(Location location, @NotNull Runnable runnable, long delay) {
      delay = this.ensureValidDuration(delay);
      return this.wrapTask(this.regionScheduler.runDelayed(this.plugin, location, (task) -> runnable.run(), delay));
   }

   public @NotNull CompletableFuture<Void> runAtLocationLater(Location location, @NotNull Consumer<WrappedTask> consumer, long delay) {
      CompletableFuture<Void> future = new CompletableFuture();
      delay = this.ensureValidDuration(delay);
      this.regionScheduler.runDelayed(this.plugin, location, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete(null);
      }, delay);
      return future;
   }

   public WrappedTask runAtLocationLater(Location location, @NotNull Runnable runnable, long delay, TimeUnit unit) {
      return this.runAtLocationLater(location, runnable, TimeConverter.toTicks(delay, unit));
   }

   public @NotNull CompletableFuture<Void> runAtLocationLater(Location location, @NotNull Consumer<WrappedTask> consumer, long delay, TimeUnit unit) {
      return this.runAtLocationLater(location, consumer, TimeConverter.toTicks(delay, unit));
   }

   public WrappedTask runAtLocationTimer(Location location, @NotNull Runnable runnable, long delay, long period) {
      delay = this.ensureValidDuration(delay);
      period = this.ensureValidDuration(period);
      return this.wrapTask(this.regionScheduler.runAtFixedRate(this.plugin, location, (task) -> runnable.run(), delay, period));
   }

   public void runAtLocationTimer(Location location, @NotNull Consumer<WrappedTask> consumer, long delay, long period) {
      delay = this.ensureValidDuration(delay);
      period = this.ensureValidDuration(period);
      this.regionScheduler.runAtFixedRate(this.plugin, location, (task) -> consumer.accept(this.wrapTask(task)), delay, period);
   }

   public WrappedTask runAtLocationTimer(Location location, @NotNull Runnable runnable, long delay, long period, TimeUnit unit) {
      return this.runAtLocationTimer(location, runnable, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public void runAtLocationTimer(Location location, @NotNull Consumer<WrappedTask> consumer, long delay, long period, TimeUnit unit) {
      this.runAtLocationTimer(location, consumer, TimeConverter.toTicks(delay, unit), TimeConverter.toTicks(period, unit));
   }

   public @NotNull CompletableFuture<EntityTaskResult> runAtEntity(Entity entity, @NotNull Consumer<WrappedTask> consumer) {
      CompletableFuture<EntityTaskResult> future = new CompletableFuture();
      ScheduledTask scheduledTask = entity.getScheduler().run(this.plugin, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete(EntityTaskResult.SUCCESS);
      }, (Runnable)null);
      if (scheduledTask == null) {
         future.complete(EntityTaskResult.SCHEDULER_RETIRED);
      }

      return future;
   }

   public @NotNull CompletableFuture<EntityTaskResult> runAtEntityWithFallback(Entity entity, @NotNull Consumer<WrappedTask> consumer, Runnable fallback) {
      CompletableFuture<EntityTaskResult> future = new CompletableFuture();
      ScheduledTask scheduledTask = entity.getScheduler().run(this.plugin, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete(EntityTaskResult.SUCCESS);
      }, () -> {
         fallback.run();
         future.complete(EntityTaskResult.ENTITY_RETIRED);
      });
      if (scheduledTask == null) {
         future.complete(EntityTaskResult.SCHEDULER_RETIRED);
      }

      return future;
   }

   public WrappedTask runAtEntityLater(Entity entity, @NotNull Runnable runnable, long delay) {
      return this.runAtEntityLater(entity, runnable, (Runnable)null, delay);
   }

   public WrappedTask runAtEntityLater(Entity entity, @NotNull Runnable runnable, Runnable fallback, long delay) {
      delay = this.ensureValidDuration(delay);
      return this.wrapTask(entity.getScheduler().runDelayed(this.plugin, (task) -> runnable.run(), fallback, delay));
   }

   public @NotNull CompletableFuture<Void> runAtEntityLater(Entity entity, @NotNull Consumer<WrappedTask> consumer, long delay) {
      return this.runAtEntityLater(entity, (Consumer)consumer, (Runnable)null, delay);
   }

   public @NotNull CompletableFuture<Void> runAtEntityLater(Entity entity, @NotNull Consumer<WrappedTask> consumer, Runnable fallback, long delay) {
      CompletableFuture<Void> future = new CompletableFuture();
   Runnable originalFallback = fallback;
   if (originalFallback != null) {
   fallback = () -> {
   originalFallback.run();
   future.complete(null);
   };
   }

      delay = this.ensureValidDuration(delay);
      entity.getScheduler().runDelayed(this.plugin, (task) -> {
         consumer.accept(this.wrapTask(task));
         future.complete(null);
      }, fallback, delay);
      return future;
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

   public WrappedTask runAtEntityTimer(Entity entity, @NotNull Runnable runnable, Runnable fallback, long delay, long period) {
      delay = this.ensureValidDuration(delay);
      period = this.ensureValidDuration(period);
      return this.wrapTask(entity.getScheduler().runAtFixedRate(this.plugin, (task) -> runnable.run(), fallback, delay, period));
   }

   public void runAtEntityTimer(Entity entity, @NotNull Consumer<WrappedTask> consumer, long delay, long period) {
      this.runAtEntityTimer(entity, (Consumer)consumer, (Runnable)null, delay, period);
   }

   public void runAtEntityTimer(Entity entity, @NotNull Consumer<WrappedTask> consumer, Runnable fallback, long delay, long period) {
      delay = this.ensureValidDuration(delay);
      period = this.ensureValidDuration(period);
      entity.getScheduler().runAtFixedRate(this.plugin, (task) -> consumer.accept(this.wrapTask(task)), fallback, delay, period);
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
      this.globalRegionScheduler.cancelTasks(this.plugin);
      this.asyncScheduler.cancelTasks(this.plugin);
   }

   public List<WrappedTask> getAllTasks() {
      try {
         return (List)this.getAllScheduledTasks().stream().filter((task) -> task.getOwningPlugin().equals(this.plugin)).map(this::wrapTask).collect(Collectors.toList());
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public List<WrappedTask> getAllServerTasks() {
      try {
         return (List)this.getAllScheduledTasks().stream().map(this::wrapTask).collect(Collectors.toList());
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   private @NotNull List<ScheduledTask> getAllScheduledTasks() throws NoSuchFieldException, IllegalAccessException {
      Class<? extends GlobalRegionScheduler> globalClass = this.globalRegionScheduler.getClass();
      Field tasksByDeadlineField = globalClass.getDeclaredField("tasksByDeadline");
      boolean wasAccessible = tasksByDeadlineField.isAccessible();
      tasksByDeadlineField.setAccessible(true);
      Long2ObjectOpenHashMap<List<ScheduledTask>> globalTasksMap = (Long2ObjectOpenHashMap)tasksByDeadlineField.get(this.globalRegionScheduler);
      tasksByDeadlineField.setAccessible(wasAccessible);
      Class<? extends AsyncScheduler> asyncClass = this.asyncScheduler.getClass();
      Field asyncTasksField = asyncClass.getDeclaredField("tasks");
      wasAccessible = asyncTasksField.isAccessible();
      asyncTasksField.setAccessible(true);
      Set<ScheduledTask> asyncTasks = (Set)asyncTasksField.get(this.asyncScheduler);
      asyncTasksField.setAccessible(wasAccessible);
      List<ScheduledTask> globalTasks = new ArrayList();
      ObjectIterator var9 = globalTasksMap.values().iterator();

      while(var9.hasNext()) {
         List<ScheduledTask> list = (List)var9.next();
         globalTasks.addAll(list);
      }

      List<ScheduledTask> allTasks = new ArrayList(globalTasks.size() + asyncTasks.size());
      allTasks.addAll(globalTasks);
      allTasks.addAll(asyncTasks);
      return allTasks;
   }

   public Player getPlayer(String name) {
      return this.plugin.getServer().getPlayer(name);
   }

   public Player getPlayerExact(String name) {
      return this.plugin.getServer().getPlayerExact(name);
   }

   public Player getPlayer(UUID uuid) {
      return this.plugin.getServer().getPlayer(uuid);
   }

   public CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
      return entity.teleportAsync(location);
   }

   public CompletableFuture<Boolean> teleportAsync(Entity entity, Location location, PlayerTeleportEvent.TeleportCause cause) {
      return entity.teleportAsync(location, cause);
   }

   public WrappedTask wrapTask(@NotNull Object nativeTask) {
      Objects.requireNonNull(nativeTask, "nativeTask");
      if (!(nativeTask instanceof ScheduledTask)) {
         throw new IllegalArgumentException("The nativeTask provided must be a ScheduledTask. Got: " + nativeTask.getClass().getName() + " instead.");
      } else {
         return new WrappedFoliaTask((ScheduledTask)nativeTask);
      }
   }

   private long ensureValidDuration(long duration) {
      if (duration <= 0L) {
         this.tickNotifier.notifyOnce(duration);
         return 1L;
      } else {
         return duration;
      }
   }
}
