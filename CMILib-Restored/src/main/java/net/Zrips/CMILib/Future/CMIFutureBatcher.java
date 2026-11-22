package net.Zrips.CMILib.Future;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CMIFutureBatcher {
  private Queue<CompletableFuture<?>> queue = new ConcurrentLinkedQueue<>();
  private boolean running = false;
  private int delayBetweenBatches = 0;

  public CMIFutureBatcher() {}

  public CMIFutureBatcher(int delayBetweenBatches) {
    this.delayBetweenBatches = delayBetweenBatches;
  }

  public CompletableFuture<?> addBatch(CompletableFuture<?> future) {
    this.queue.add(future);
    this.processQueue();
    return future;
  }

  public void processQueue() {
    if (!this.running || this.queue.isEmpty()) {
      this.running = true;
      CompletableFuture.runAsync(
          () -> {
            while (!this.queue.isEmpty()) {
              CompletableFuture<?> future = this.queue.poll();
              if (future != null) {
                if (this.delayBetweenBatches > 0) {
                  try {
                    Thread.sleep(this.delayBetweenBatches);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                  }
                }

                future.complete(null);
                continue;
              }
              break;
            }

            this.running = false;
          });
    }
  }

  public int getDelayBetweenBatches() {
    return this.delayBetweenBatches;
  }

  public void setDelayBetweenBatches(int delayBetweenBatches) {
    this.delayBetweenBatches = delayBetweenBatches;
  }

  public int getQueueSize() {
    return this.queue.size();
  }

  public boolean isRunning() {
    return this.running;
  }
}
