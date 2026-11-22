package com.Zrips.CMI.Modules.Holograms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class CMIHighFrequencyScheduler {
  private static final ScheduledExecutorService EXECUTOR =
      Executors.newScheduledThreadPool(
          Runtime.getRuntime().availableProcessors(),
          new ThreadFactory() {
            private final AtomicInteger id = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
              Thread thread = new Thread(runnable, "hf-scheduler-" + this.id.incrementAndGet());
              thread.setDaemon(true);
              return thread;
            }
          });
  private static final Map<Integer, CMIHighFrequencyScheduler.ScheduledTask> TASKS =
      new ConcurrentHashMap<>();
  private static final AtomicInteger TASK_ID_GEN = new AtomicInteger();

  public static CMIHighFrequencyScheduler.ScheduledTask runTimerAsync(
      Runnable runnable, long longValue, long period) {
    int value = TASK_ID_GEN.incrementAndGet();
    CMIHighFrequencyScheduler.ScheduledTask scheduledTask =
        new CMIHighFrequencyScheduler.ScheduledTask(value, runnable, period);
    scheduledTask.future =
        EXECUTOR.scheduleAtFixedRate(
            scheduledTask::runInternal, longValue, period, TimeUnit.MILLISECONDS);
    TASKS.put(value, scheduledTask);
    return scheduledTask;
  }

  public static CMIHighFrequencyScheduler.ScheduledTask runLaterAsync(
      Runnable runnable, long longValue) {
    int value = TASK_ID_GEN.incrementAndGet();
    CMIHighFrequencyScheduler.ScheduledTask scheduledTask =
        new CMIHighFrequencyScheduler.ScheduledTask(value, runnable, -1L);
    scheduledTask.future =
        EXECUTOR.schedule(scheduledTask::runInternal, longValue, TimeUnit.MILLISECONDS);
    TASKS.put(value, scheduledTask);
    return scheduledTask;
  }

  public static void cancelTask(int value) {
    CMIHighFrequencyScheduler.ScheduledTask scheduledTask = TASKS.remove(value);
    if (scheduledTask != null) {
      scheduledTask.cancel();
    }
  }

  public static void shutdown() {
    for (CMIHighFrequencyScheduler.ScheduledTask scheduledTask : TASKS.values()) {
      scheduledTask.cancel();
    }

    TASKS.clear();
    EXECUTOR.shutdownNow();
  }

  public static final class ScheduledTask {
    private final int id;
    private final Runnable runnable;
    private final long periodMs;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private volatile ScheduledFuture<?> future;

    private ScheduledTask(int value, Runnable runnable, long longValue) {
      this.id = value;
      this.runnable = runnable;
      this.periodMs = longValue;
    }

    private void runInternal() {
      if (!this.cancelled.get()) {
        try {
          this.runnable.run();
        } catch (Throwable throwable) {
          throwable.printStackTrace();
          this.cancel();
        } finally {
          if (this.periodMs < 0L) {
            this.cancel();
          }
        }
      }
    }

    public void cancel() {
      if (this.cancelled.compareAndSet(false, true)) {
        if (this.future != null) {
          this.future.cancel(false);
        }

        CMIHighFrequencyScheduler.TASKS.remove(this.id);
      }
    }

    public boolean isCancelled() {
      return this.cancelled.get();
    }

    public int getTaskId() {
      return this.id;
    }
  }
}
