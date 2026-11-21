package io.lumine.mythic.bukkit.utils.events.functional;

import io.lumine.mythic.bukkit.utils.logging.Log;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public interface SubscriptionBuilder<T> {
   BiConsumer<Object, Throwable> DEFAULT_EXCEPTION_CONSUMER = (event, throwable) -> {
      Log.severe("[EVENTS] Exception thrown whilst handling event: " + event.getClass().getName());
      throwable.printStackTrace();
   };

   @Nonnull
   SubscriptionBuilder<T> expireIf(@Nonnull Predicate<T> predicate);

   @Nonnull
   SubscriptionBuilder<T> expireAfter(long value, @Nonnull TimeUnit timeUnit);

   @Nonnull
   SubscriptionBuilder<T> expireAfter(long value);

   @Nonnull
   SubscriptionBuilder<T> filter(@Nonnull Predicate<T> predicate);
}
