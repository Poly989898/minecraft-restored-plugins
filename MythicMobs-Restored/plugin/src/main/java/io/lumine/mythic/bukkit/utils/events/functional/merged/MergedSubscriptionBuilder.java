package io.lumine.mythic.bukkit.utils.events.functional.merged;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import io.lumine.mythic.bukkit.utils.Delegates;
import io.lumine.mythic.bukkit.utils.events.MergedSubscription;
import io.lumine.mythic.bukkit.utils.events.functional.ExpiryTestStage;
import io.lumine.mythic.bukkit.utils.events.functional.SubscriptionBuilder;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public interface MergedSubscriptionBuilder<T> extends SubscriptionBuilder<T> {
   @Nonnull
   static <T> MergedSubscriptionBuilder<T> newBuilder(@Nonnull Class<T> handledClass) {
      Objects.requireNonNull(handledClass, "handledClass");
      return new MergedSubscriptionBuilderImpl<>(TypeToken.of(handledClass));
   }

   @Nonnull
   static <T> MergedSubscriptionBuilder<T> newBuilder(@Nonnull TypeToken<T> type) {
      Objects.requireNonNull(type, "type");
      return new MergedSubscriptionBuilderImpl<>(type);
   }

   @Nonnull
   @SafeVarargs
   static <S extends Event> MergedSubscriptionBuilder<S> newBuilder(@Nonnull Class<S> superClass, @Nonnull Class<? extends S>... eventClasses) {
      return newBuilder(superClass, EventPriority.NORMAL, eventClasses);
   }

   @Nonnull
   @SafeVarargs
   static <S extends Event> MergedSubscriptionBuilder<S> newBuilder(
      @Nonnull Class<S> superClass, @Nonnull EventPriority priority, @Nonnull Class<? extends S>... eventClasses
   ) {
      Objects.requireNonNull(superClass, "superClass");
      Objects.requireNonNull(eventClasses, "eventClasses");
      Objects.requireNonNull(priority, "priority");
      if (eventClasses.length < 2) {
         throw new IllegalArgumentException("merge method used for only one subclass");
      } else {
         MergedSubscriptionBuilderImpl<S> h = new MergedSubscriptionBuilderImpl(TypeToken.of(superClass));

         for (Class<? extends S> clazz : eventClasses) {
            h.bindEvent(clazz, priority, e -> (S)e);
         }

         return h;
      }
   }

   @Nonnull
   default MergedSubscriptionBuilder<T> expireIf(@Nonnull Predicate<T> predicate) {
      return this.expireIf(Delegates.predicateToBiPredicateSecond(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
   }

   @Nonnull
   default MergedSubscriptionBuilder<T> expireAfter(long duration, @Nonnull TimeUnit unit) {
      Objects.requireNonNull(unit, "unit");
      Preconditions.checkArgument(duration >= 1L, "duration < 1");
      long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
      return this.expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
   }

   @Nonnull
   default MergedSubscriptionBuilder<T> expireAfter(long maxCalls) {
      Preconditions.checkArgument(maxCalls >= 1L, "maxCalls < 1");
      return this.expireIf((handler, event) -> handler.getCallCounter() >= maxCalls, ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
   }

   @Nonnull
   MergedSubscriptionBuilder<T> filter(@Nonnull Predicate<T> predicate);

   @Nonnull
   MergedSubscriptionBuilder<T> expireIf(@Nonnull BiPredicate<MergedSubscription<T>, T> biPredicate, @Nonnull ExpiryTestStage... expiryTestStages);

   @Nonnull
   <E extends Event> MergedSubscriptionBuilder<T> bindEvent(@Nonnull Class<E> clazz, @Nonnull Function<E, T> function);

   @Nonnull
   <E extends Event> MergedSubscriptionBuilder<T> bindEvent(@Nonnull Class<E> clazz, @Nonnull EventPriority eventPriority, @Nonnull Function<E, T> function);

   @Nonnull
   MergedSubscriptionBuilder<T> exceptionConsumer(@Nonnull BiConsumer<Event, Throwable> biConsumer);

   @Nonnull
   MergedHandlerList<T> handlers();

   @Nonnull
   default MergedSubscription<T> handler(@Nonnull Consumer<? super T> handler) {
      return this.handlers().consumer(handler).register();
   }

   @Nonnull
   default MergedSubscription<T> biHandler(@Nonnull BiConsumer<MergedSubscription<T>, ? super T> handler) {
      return this.handlers().biConsumer(handler).register();
   }
}
