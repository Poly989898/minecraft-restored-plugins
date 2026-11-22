package net.Zrips.CMILib.Version.Schedulers;

import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.CMILib;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class CMIBukkitImpl implements CMIBaseImpl {
  private final JavaPlugin plugin;
  private final BukkitScheduler scheduler;

  public CMIBukkitImpl(CMILib plugin) {
    this.plugin = plugin;
    this.scheduler = this.plugin.getServer().getScheduler();
  }

  @Override
  public CompletableFuture<Void> runTask(Runnable runnable) {
    return this.runTask(this.plugin, runnable);
  }

  @Override
  public CompletableFuture<Void> runTask(JavaPlugin plugin, Runnable runnable) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    this.scheduler.runTask(
        plugin,
        () -> {
          runnable.run();
          future.complete(null);
        });
    return future;
  }

  @Override
  public CompletableFuture<Void> runTaskAsynchronously(Runnable runnable) {
    return this.runTaskAsynchronously(this.plugin, runnable);
  }

  @Override
  public CompletableFuture<Void> runTaskAsynchronously(JavaPlugin plugin, Runnable runnable) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    this.scheduler.runTaskAsynchronously(
        plugin,
        () -> {
          runnable.run();
          future.complete(null);
        });
    return future;
  }

  @Override
  public CMITask runTaskLater(Runnable runnable, long delay) {
    return this.runTaskLater(this.plugin, runnable, delay);
  }

  @Override
  public CMITask runTaskLater(JavaPlugin plugin, Runnable runnable, long delay) {
    return new CMIBukkitTask(this.scheduler.runTaskLater(plugin, runnable, delay));
  }

  @Override
  public CMITask runLaterAsync(Runnable runnable, long delay) {
    return this.runLaterAsync(this.plugin, runnable, delay);
  }

  @Override
  public CMITask runLaterAsync(JavaPlugin plugin, Runnable runnable, long delay) {
    return new CMIBukkitTask(this.scheduler.runTaskLaterAsynchronously(plugin, runnable, delay));
  }

  @Override
  public CMITask scheduleSyncRepeatingTask(Runnable runnable, long delay, long period) {
    return this.scheduleSyncRepeatingTask(this.plugin, runnable, delay, period);
  }

  @Override
  public CMITask scheduleSyncRepeatingTask(
      JavaPlugin plugin, Runnable runnable, long delay, long period) {
    return new CMIBukkitTask(this.scheduler.runTaskTimer(plugin, runnable, delay, period));
  }

  @Override
  public CMITask runTimerAsync(Runnable runnable, long delay, long period) {
    return this.runTimerAsync(this.plugin, runnable, delay, period);
  }

  @Override
  public CMITask runTimerAsync(JavaPlugin plugin, Runnable runnable, long delay, long period) {
    return new CMIBukkitTask(
        this.scheduler.runTaskTimerAsynchronously(plugin, runnable, delay, period));
  }

  @Override
  public CompletableFuture<Void> runAtLocation(Location location, Runnable runnable) {
    return this.runAtLocation(this.plugin, null, 0, 0, runnable);
  }

  @Override
  public CompletableFuture<Void> runAtLocation(
      JavaPlugin plugin, Location location, Runnable runnable) {
    return this.runAtLocation(plugin, null, 0, 0, runnable);
  }

  @Override
  public CompletableFuture<Void> runAtLocation(Chunk chunk, Runnable runnable) {
    return this.runAtLocation(this.plugin, null, 0, 0, runnable);
  }

  @Override
  public CompletableFuture<Void> runAtLocation(JavaPlugin plugin, Chunk chunk, Runnable runnable) {
    return this.runAtLocation(plugin, null, 0, 0, runnable);
  }

  @Override
  public CompletableFuture<Void> runAtLocation(World world, int x, int z, Runnable runnable) {
    return this.runAtLocation(this.plugin, world, x, z, runnable);
  }

  @Override
  public CompletableFuture<Void> runAtLocation(
      JavaPlugin plugin, World world, int x, int z, Runnable runnable) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    this.scheduler.runTask(
        plugin,
        () -> {
          runnable.run();
          future.complete(null);
        });
    return future;
  }

  @Override
  public CMITask runAtLocationLater(Location location, Runnable runnable, long delay) {
    return this.runAtLocationLater(this.plugin, location, runnable, delay);
  }

  @Override
  public CMITask runAtLocationLater(
      JavaPlugin plugin, Location location, Runnable runnable, long delay) {
    return new CMIBukkitTask(this.scheduler.runTaskLater(plugin, runnable, delay));
  }

  @Override
  public CMITask runAtLocationTimer(Location location, Runnable runnable, long delay, long period) {
    return this.runAtLocationTimer(this.plugin, location, runnable, delay, period);
  }

  @Override
  public CMITask runAtLocationTimer(
      JavaPlugin plugin, Location location, Runnable runnable, long delay, long period) {
    return new CMIBukkitTask(this.scheduler.runTaskTimer(plugin, runnable, delay, period));
  }

  @Override
  public CompletableFuture<CMITaskResult> runAtEntity(Entity entity, Runnable runnable) {
    return this.runAtEntity(this.plugin, entity, runnable);
  }

  @Override
  public CompletableFuture<CMITaskResult> runAtEntity(
      JavaPlugin plugin, Entity entity, Runnable runnable) {
    CompletableFuture<CMITaskResult> future = new CompletableFuture<>();
    this.scheduler.runTask(
        plugin,
        () -> {
          runnable.run();
          future.complete(CMITaskResult.SUCCESS);
        });
    return future;
  }

  @Override
  public CompletableFuture<CMITaskResult> runAtEntityWithFallback(
      Entity entity, Runnable runnable, Runnable fallback) {
    return this.runAtEntityWithFallback(this.plugin, entity, runnable, fallback);
  }

  @Override
  public CompletableFuture<CMITaskResult> runAtEntityWithFallback(
      JavaPlugin plugin, Entity entity, Runnable runnable, Runnable fallback) {
    CompletableFuture<CMITaskResult> future = new CompletableFuture<>();
    this.scheduler.runTask(
        plugin,
        () -> {
          if (entity.isValid()) {
            runnable.run();
            future.complete(CMITaskResult.SUCCESS);
          } else {
            fallback.run();
            future.complete(CMITaskResult.ENTITY_RETIRED);
          }
        });
    return future;
  }

  @Override
  public CMITask runAtEntityLater(Entity entity, Runnable runnable, long delay) {
    return this.runAtEntityLater(this.plugin, entity, runnable, delay);
  }

  @Override
  public CMITask runAtEntityLater(JavaPlugin plugin, Entity entity, Runnable runnable, long delay) {
    return new CMIBukkitTask(this.scheduler.runTaskLater(plugin, runnable, delay));
  }

  @Override
  public CMITask runAtEntityTimer(Entity entity, Runnable runnable, long delay, long period) {
    return this.runAtEntityTimer(this.plugin, entity, runnable, delay, period);
  }

  @Override
  public CMITask runAtEntityTimer(
      JavaPlugin plugin, Entity entity, Runnable runnable, long delay, long period) {
    return new CMIBukkitTask(this.scheduler.runTaskTimer(plugin, runnable, delay, period));
  }
}
