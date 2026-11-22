package com.Zrips.CMI.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CMIThread {
  private static final AtomicInteger COUNTER = new AtomicInteger(1);
  private static final ExecutorService EXECUTOR =
      Executors.newFixedThreadPool(
          Math.max(2, Runtime.getRuntime().availableProcessors() - 1),
          runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("CMI-Thread-" + COUNTER.getAndIncrement());
            thread.setDaemon(true);
            return thread;
          });

  public static ExecutorService getExecutor() {
    return EXECUTOR;
  }

  public static void shutdown() {
    EXECUTOR.shutdown();
  }
}
