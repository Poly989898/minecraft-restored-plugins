package io.lumine.mythic.bukkit.utils.events.functional.single;

import com.google.common.base.Preconditions;
import io.lumine.mythic.bukkit.utils.Delegates;
import io.lumine.mythic.bukkit.utils.events.SingleSubscription;
import io.lumine.mythic.bukkit.utils.events.functional.ExpiryTestStage;
import io.lumine.mythic.bukkit.utils.events.functional.SubscriptionBuilder;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public interface SingleSubscriptionBuilder<T extends Event> extends SubscriptionBuilder<T> {
   @Nonnull
   static <T extends Event> SingleSubscriptionBuilder<T> newBuilder(@Nonnull Class<T> eventClass) {
      return newBuilder(eventClass, EventPriority.NORMAL);
   }

   @Nonnull
   static <T extends Event> SingleSubscriptionBuilder<T> newBuilder(@Nonnull Class<T> eventClass, @Nonnull EventPriority priority) {
      Objects.requireNonNull(eventClass, "eventClass");
      Objects.requireNonNull(priority, "priority");
      return new SingleSubscriptionBuilderImpl<>(eventClass, priority);
   }

   @Nonnull
   default SingleSubscriptionBuilder<T> expireIf(@Nonnull Predicate<T> predicate) {
      return this.expireIf(Delegates.predicateToBiPredicateSecond(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
   }

   @Nonnull
   default SingleSubscriptionBuilder<T> expireAfter(long duration, @Nonnull TimeUnit unit) {
      Objects.requireNonNull(unit, "unit");
      Preconditions.checkArgument(duration >= 1L, "duration < 1");
      long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
      return this.expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
   }

   @Nonnull
   default SingleSubscriptionBuilder<T> expireAfter(long maxCalls) {
      Preconditions.checkArgument(maxCalls >= 1L, "maxCalls < 1");
      return this.expireIf((handler, event) -> handler.getCallCounter() >= maxCalls, ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
   }

   @Nonnull
   SingleSubscriptionBuilder<T> filter(@Nonnull Predicate<T> predicate);

   @Nonnull
   SingleSubscriptionBuilder<T> expireIf(@Nonnull BiPredicate<SingleSubscription<T>, T> biPredicate, @Nonnull ExpiryTestStage... expiryTestStages);

   @Nonnull
   SingleSubscriptionBuilder<T> exceptionConsumer(@Nonnull BiConsumer<? super T, Throwable> biConsumer);

   @Nonnull
   SingleSubscriptionBuilder<T> handleSubclasses();

   @Nonnull
   SingleHandlerList<T> handlers();

   @Nonnull
   default SingleSubscription<T> handler(@Nonnull Consumer<? super T> handler) {
      return this.handlers().consumer(handler).register();
   }

   @Nonnull
   default SingleSubscription<T> biHandler(@Nonnull BiConsumer<SingleSubscription<T>, ? super T> handler) {
      return this.handlers().biConsumer(handler).register();
   }
}
