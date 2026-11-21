package com.ticxo.modelengine.api.utils.ticker;

import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Task {
   private final Consumer<Task> task;
   @Getter
   private final int startDelay;
   @Getter
   private final int interval;
   @Getter
   private final boolean isRepeating;
   @Getter
   private int delay;
   @Getter
   private int tick;
   @Getter
   private int runCount;
   @Getter
   private boolean canceled;

   public boolean tick() {
      if (this.delay++ >= this.startDelay && this.tick-- <= 0) {
         this.tick = this.interval;
         this.task.accept(this);
         this.runCount++;
         return this.canceled || !this.isRepeating;
      } else {
         return this.canceled;
      }
   }

   public void cancel() {
      this.canceled = true;
   }
}
