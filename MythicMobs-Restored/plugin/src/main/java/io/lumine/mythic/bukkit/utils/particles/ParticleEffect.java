package io.lumine.mythic.bukkit.utils.particles;

import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;

public class ParticleEffect {
   public abstract class ParticleEffectAnimator implements Runnable, Terminable {
      private final int interval;
      private Task task;
      private int iteration = 0;

      public void start() {
         this.task = Schedulers.async().runRepeating(this, 0L, (long)this.interval);
      }

      @Override
      public void run() {
         this.tick(this.iteration);
         this.iteration++;
      }

      public abstract void tick(int value);

      @Override
      public void close() throws Exception {
         this.task.terminate();
      }

      public ParticleEffectAnimator(final int interval) {
         this.interval = interval;
      }

      public int getInterval() {
         return this.interval;
      }

      public Task getTask() {
         return this.task;
      }

      public int getIteration() {
         return this.iteration;
      }

      public void setTask(Task task) {
         this.task = task;
      }

      public void setIteration(int iteration) {
         this.iteration = iteration;
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof ParticleEffect.ParticleEffectAnimator other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.getInterval() != other.getInterval()) {
            return false;
         } else if (this.getIteration() != other.getIteration()) {
            return false;
         } else {
            Object this$task = this.getTask();
            Object other$task = other.getTask();
            return this$task == null ? other$task == null : this$task.equals(other$task);
         }
      }

      protected boolean canEqual(Object other) {
         return other instanceof ParticleEffect.ParticleEffectAnimator;
      }

      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + this.getInterval();
         result = result * 59 + this.getIteration();
         Object $task = this.getTask();
         return result * 59 + ($task == null ? 43 : $task.hashCode());
      }

      @Override
      public String toString() {
         return "ParticleEffect.ParticleEffectAnimator(interval="
            + this.getInterval()
            + ", task="
            + this.getTask()
            + ", iteration="
            + this.getIteration()
            + ")";
      }
   }
}
