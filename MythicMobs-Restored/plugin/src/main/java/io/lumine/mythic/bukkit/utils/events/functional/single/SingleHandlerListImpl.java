package io.lumine.mythic.bukkit.utils.events.functional.single;

import io.lumine.mythic.bukkit.utils.events.SingleSubscription;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import org.bukkit.event.Event;

class SingleHandlerListImpl<T extends Event> implements SingleHandlerList<T> {
   private final SingleSubscriptionBuilderImpl<T> builder;
   private final List<BiConsumer<SingleSubscription<T>, ? super T>> handlers = new ArrayList<>(1);

   SingleHandlerListImpl(@Nonnull SingleSubscriptionBuilderImpl<T> builder) {
      this.builder = builder;
   }

   @Nonnull
   @Override
   public SingleHandlerList<T> biConsumer(@Nonnull BiConsumer<SingleSubscription<T>, ? super T> handler) {
      Objects.requireNonNull(handler, "handler");
      this.handlers.add(handler);
      return this;
   }

   @Nonnull
   public SingleSubscription<T> register() {
      if (this.handlers.isEmpty()) {
         throw new IllegalStateException("No handlers have been registered");
      } else {
         EventListener<T> listener = new EventListener<>(this.builder, this.handlers);
         listener.register(LoaderUtils.getPlugin());
         return listener;
      }
   }
}
