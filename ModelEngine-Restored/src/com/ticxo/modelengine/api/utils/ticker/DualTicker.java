package com.ticxo.modelengine.api.utils.ticker;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.scheduling.PlatformScheduler;
import java.util.function.Consumer;
import org.bukkit.plugin.java.JavaPlugin;

public class DualTicker {
   private final PseudoThread sync;
   private final PseudoThread async;

   public DualTicker(JavaPlugin plugin, PlatformScheduler scheduler) {
      this.sync = new PseudoThread("sync", scheduler, plugin, false, 0, 0, false, false);
      this.async = new PseudoThread("async", scheduler, plugin, true, 0, 0, false, false);
   }

   public static void queueSyncTask(Runnable runnable) {
      queueDelayedSyncTask(task -> runnable.run(), 0);
   }

   public static void queueDelayedSyncTask(Runnable runnable, int delay) {
      queueDelayedSyncTask(task -> runnable.run(), delay);
   }

   public static void queueRepeatingSyncTask(Runnable runnable, int delay, int interval) {
      queueRepeatingSyncTask(task -> runnable.run(), delay, interval);
   }

   public static void queueSyncTask(Consumer<Task> consumer) {
      queueDelayedSyncTask(consumer, 0);
   }

   public static void queueDelayedSyncTask(Consumer<Task> consumer, int delay) {
      DualTicker ticker = ModelEngineAPI.getAPI().getTicker();
      ticker.sync.queueTask(new Task(consumer, delay, 0, false));
   }

   public static void queueRepeatingSyncTask(Consumer<Task> consumer, int delay, int interval) {
      DualTicker ticker = ModelEngineAPI.getAPI().getTicker();
      ticker.sync.queueTask(new Task(consumer, delay, interval, true));
   }

   public static void queueAsyncTask(Runnable runnable) {
      queueDelayedAsyncTask(task -> runnable.run(), 0);
   }

   public static void queueDelayedAsyncTask(Runnable runnable, int delay) {
      queueDelayedAsyncTask(task -> runnable.run(), delay);
   }

   public static void queueRepeatingAsyncTask(Runnable runnable, int delay, int interval) {
      queueRepeatingAsyncTask(task -> runnable.run(), delay, interval);
   }

   public static void queueAsyncTask(Consumer<Task> consumer) {
      queueDelayedAsyncTask(consumer, 0);
   }

   public static void queueDelayedAsyncTask(Consumer<Task> consumer, int delay) {
      DualTicker ticker = ModelEngineAPI.getAPI().getTicker();
      ticker.async.queueTask(new Task(consumer, delay, 0, false));
   }

   public static void queueRepeatingAsyncTask(Consumer<Task> consumer, int delay, int interval) {
      DualTicker ticker = ModelEngineAPI.getAPI().getTicker();
      ticker.async.queueTask(new Task(consumer, delay, interval, true));
   }

   public void start() {
      this.sync.start();
      this.async.start();
      ModelEngineAPI.getAPI().getDataTrackers().start();
      ModelEngineAPI.getAPI().getModelUpdaters().start();
   }

   public void stop() {
      this.sync.end();
      this.async.end();
      ModelEngineAPI.getAPI().getDataTrackers().end();
      ModelEngineAPI.getAPI().getModelUpdaters().end();
   }
}
