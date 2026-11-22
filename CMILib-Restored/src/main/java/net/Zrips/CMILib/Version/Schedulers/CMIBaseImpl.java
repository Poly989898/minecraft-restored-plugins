package net.Zrips.CMILib.Version.Schedulers;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public interface CMIBaseImpl {
  CompletableFuture<Void> runTask(Runnable task);

  CompletableFuture<Void> runTaskAsynchronously(Runnable task);

  CMITask runTaskLater(Runnable task, long delay);

  CMITask runLaterAsync(Runnable task, long delay);

  CMITask scheduleSyncRepeatingTask(Runnable task, long delay, long period);

  CMITask runTimerAsync(Runnable task, long delay, long period);

  CompletableFuture<Void> runAtLocation(Location location, Runnable task);

  CMITask runAtLocationLater(Location location, Runnable task, long delay);

  CMITask runAtLocationTimer(Location location, Runnable task, long delay, long period);

  CompletableFuture<CMITaskResult> runAtEntity(Entity entity, Runnable task);

  CompletableFuture<CMITaskResult> runAtEntityWithFallback(
      Entity entity, Runnable task, Runnable retiredTask);

  CMITask runAtEntityLater(Entity entity, Runnable task, long delay);

  CMITask runAtEntityTimer(Entity entity, Runnable task, long delay, long period);

  CompletableFuture<Void> runAtLocation(Chunk chunk, Runnable task);

  CompletableFuture<Void> runAtLocation(World world, int x, int z, Runnable task);

  CompletableFuture<Void> runTask(JavaPlugin plugin, Runnable task);

  CompletableFuture<Void> runTaskAsynchronously(JavaPlugin plugin, Runnable task);

  CMITask runTaskLater(JavaPlugin plugin, Runnable task, long delay);

  CMITask runLaterAsync(JavaPlugin plugin, Runnable task, long delay);

  CMITask scheduleSyncRepeatingTask(JavaPlugin plugin, Runnable task, long delay, long period);

  CMITask runTimerAsync(JavaPlugin plugin, Runnable task, long delay, long period);

  CompletableFuture<Void> runAtLocation(JavaPlugin plugin, Location location, Runnable task);

  CMITask runAtLocationLater(JavaPlugin plugin, Location location, Runnable task, long delay);

  CMITask runAtLocationTimer(
      JavaPlugin plugin, Location location, Runnable task, long delay, long period);

  CompletableFuture<CMITaskResult> runAtEntity(JavaPlugin plugin, Entity entity, Runnable task);

  CompletableFuture<CMITaskResult> runAtEntityWithFallback(
      JavaPlugin plugin, Entity entity, Runnable task, Runnable retiredTask);

  CMITask runAtEntityLater(JavaPlugin plugin, Entity entity, Runnable task, long delay);

  CMITask runAtEntityTimer(
      JavaPlugin plugin, Entity entity, Runnable task, long delay, long period);

  CompletableFuture<Void> runAtLocation(JavaPlugin plugin, Chunk chunk, Runnable task);

  CompletableFuture<Void> runAtLocation(
      JavaPlugin plugin, World world, int x, int z, Runnable task);
}
