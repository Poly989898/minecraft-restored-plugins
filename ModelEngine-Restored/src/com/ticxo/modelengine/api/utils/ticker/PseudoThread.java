package com.ticxo.modelengine.api.utils.ticker;

import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.scheduling.PlatformScheduler;
import com.ticxo.modelengine.api.utils.scheduling.PlatformTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class PseudoThread {
   @Getter
   private final String name;
   private final PlatformScheduler scheduler;
   private final JavaPlugin plugin;
   private final boolean isAsync;
   private final int delay;
   private final int period;
   private final boolean canWait;
   private final boolean canMultiTick;
   private final Queue<Task> taskQueue = new ConcurrentLinkedQueue<>();
   private final List<Task> tasks = new ArrayList<>();
   private final List<Consumer<Integer>> overloadCallback = new ArrayList<>();
   private PlatformTask tickTask;
   private boolean locked;
   private int skipped;
   private long lastTick = System.nanoTime();

   public void start() {
      if (this.isAsync) {
         this.tickTask = this.scheduler.scheduleRepeatingAsync(this.plugin, this::tick, this.delay, this.period);
      } else {
         this.tickTask = this.scheduler.scheduleRepeating(this.plugin, this::tick, this.delay, this.period);
      }
   }

   public void end() {
      this.taskQueue.clear();
      this.tasks.clear();
      if (this.tickTask != null) {
         this.tickTask.cancel();
      }
   }

   public void queueTask(Task task) {
      this.taskQueue.add(task);
   }

   public void registerOverloadCallback(Consumer<Integer> callback) {
      this.overloadCallback.add(callback);
   }

   private void tick() {
      long currentTick = System.currentTimeMillis();
      long nowNanos = System.nanoTime();
      if (this.canMultiTick || nowNanos - this.lastTick >= 45000000L) {
         this.lastTick = nowNanos;
         if (this.locked) {
            this.skipped++;
         } else {
            this.locked = true;
            long timings = currentTick;

            while (!this.taskQueue.isEmpty()) {
               this.tasks.add(this.taskQueue.poll());
            }

            this.tasks.removeIf(task -> {
               try {
                  return task.tick();
               } catch (Throwable t) {
                  t.printStackTrace();
                  return task.isCanceled() || !task.isRepeating();
               }
            });
            timings = System.currentTimeMillis() - timings;
            this.locked = false;
            if (this.skipped > 0) {
               this.overloadCallback.forEach(callback -> callback.accept(this.skipped));
               if (!this.canWait) {
                  TLogger.debug(
                     "The pseudo thread ["
                        + this.name
                        + "] has skipped "
                        + this.skipped
                        + (this.skipped == 1 ? " tick" : " ticks")
                        + " ("
                        + timings
                        + "ms). Is it overloaded?"
                  );
               }

               this.skipped = 0;
            }
         }
      }
   }
}
