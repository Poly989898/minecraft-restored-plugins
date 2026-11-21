package io.lumine.mythic.bukkit.utils.network.messaging.util;

import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.network.messaging.Channel;
import io.lumine.mythic.bukkit.utils.promise.ThreadContext;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public final class ChannelPublisher<T> implements Terminable {
   private final Channel<T> channel;
   private final Supplier<? extends T> supplier;
   private final Task task;

   @Nonnull
   public static <T> ChannelPublisher<T> create(
      @Nonnull Channel<T> channel, long duration, @Nonnull TimeUnit unit, @Nonnull ThreadContext threadContext, @Nonnull Supplier<? extends T> supplier
   ) {
      Objects.requireNonNull(channel, "channel");
      Objects.requireNonNull(unit, "unit");
      Objects.requireNonNull(threadContext, "threadContext");
      Objects.requireNonNull(supplier, "supplier");
      return new ChannelPublisher<>(channel, supplier, duration, unit, threadContext);
   }

   @Nonnull
   public static <T> ChannelPublisher<T> create(@Nonnull Channel<T> channel, long duration, @Nonnull TimeUnit unit, @Nonnull Supplier<? extends T> supplier) {
      return create(channel, duration, unit, ThreadContext.ASYNC, supplier);
   }

   private ChannelPublisher(Channel<T> channel, Supplier<? extends T> supplier, long duration, TimeUnit unit, ThreadContext threadContext) {
      this.channel = channel;
      this.supplier = supplier;
      this.task = Schedulers.builder().on(threadContext).afterAndEvery(duration, unit).run(this::submit);
   }

   public Channel<T> getChannel() {
      return this.channel;
   }

   private void submit() {
      this.channel.sendMessage((T)this.supplier.get());
   }

   @Override
   public void close() {
      this.task.close();
   }
}
