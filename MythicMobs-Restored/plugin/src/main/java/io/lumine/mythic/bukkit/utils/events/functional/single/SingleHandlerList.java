package io.lumine.mythic.bukkit.utils.events.functional.single;

import io.lumine.mythic.bukkit.utils.Delegates;
import io.lumine.mythic.bukkit.utils.events.SingleSubscription;
import io.lumine.mythic.bukkit.utils.events.functional.FunctionalHandlerList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.bukkit.event.Event;

public interface SingleHandlerList<T extends Event> extends FunctionalHandlerList<T, SingleSubscription<T>> {
   @Nonnull
   default SingleHandlerList<T> consumer(@Nonnull Consumer<? super T> handler) {
      Objects.requireNonNull(handler, "handler");
      return this.biConsumer(Delegates.consumerToBiConsumerSecond(handler));
   }

   @Nonnull
   SingleHandlerList<T> biConsumer(@Nonnull BiConsumer<SingleSubscription<T>, ? super T> biConsumer);
}
