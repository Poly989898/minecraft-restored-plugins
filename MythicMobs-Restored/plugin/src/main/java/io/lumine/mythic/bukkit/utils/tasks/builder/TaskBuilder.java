package io.lumine.mythic.bukkit.utils.tasks.builder;

import io.lumine.mythic.bukkit.utils.promise.ThreadContext;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public interface TaskBuilder {
   @Nonnull
   static TaskBuilder newBuilder() {
      return TaskBuilderImpl.INSTANCE;
   }

   @Nonnull
   default TaskBuilder.ThreadContextual on(@Nonnull ThreadContext context) {
      switch (context) {
         case SYNC:
            return this.sync();
         case ASYNC:
            return this.async();
         default:
            throw new AssertionError();
      }
   }

   @Nonnull
   TaskBuilder.ThreadContextual sync();

   @Nonnull
   TaskBuilder.ThreadContextual async();

   public interface Delayed extends ContextualPromiseBuilder {
   }

   public interface DelayedTick extends TaskBuilder.Delayed {
      @Nonnull
      ContextualTaskBuilder every(long value);
   }

   public interface DelayedTime extends TaskBuilder.Delayed {
      @Nonnull
      ContextualTaskBuilder every(long value, TimeUnit timeUnit);
   }

   public interface ThreadContextual {
      @Nonnull
      ContextualPromiseBuilder now();

      @Nonnull
      TaskBuilder.DelayedTick after(long value);

      @Nonnull
      TaskBuilder.DelayedTime after(long value, @Nonnull TimeUnit timeUnit);

      @Nonnull
      ContextualTaskBuilder afterAndEvery(long value);

      @Nonnull
      ContextualTaskBuilder afterAndEvery(long value, @Nonnull TimeUnit timeUnit);

      @Nonnull
      ContextualTaskBuilder every(long value);

      @Nonnull
      ContextualTaskBuilder every(long value, @Nonnull TimeUnit timeUnit);
   }
}
