package com.nexomc.libs.actions.tasks;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class DefaultTaskProcessor implements TaskProcessor {
   private final ExecutorService asyncService = Executors.newCachedThreadPool();
   private final ScheduledExecutorService asyncDelayedService = Executors.newScheduledThreadPool(10);
   private final Timer syncTimer = new Timer();

   public void runAsync(@NotNull Runnable task) {
      this.asyncService.submit(task);
   }

   public void runAsync(@NotNull Runnable task, long delay) {
      this.asyncDelayedService.schedule(task, delay, TimeUnit.MILLISECONDS);
   }

   public void runSync(@NotNull Runnable task) {
      task.run();
   }

   public void runSync(final @NotNull Runnable task, long delay) {
      this.syncTimer.schedule(new TimerTask() {
         public void run() {
            task.run();
         }
      }, delay);
   }
}
