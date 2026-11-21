package io.lumine.mythic.bukkit.utils.events.functional.merged;

import io.lumine.mythic.bukkit.utils.Delegates;
import io.lumine.mythic.bukkit.utils.events.MergedSubscription;
import io.lumine.mythic.bukkit.utils.events.functional.FunctionalHandlerList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public interface MergedHandlerList<T> extends FunctionalHandlerList<T, MergedSubscription<T>> {
   @Nonnull
   default MergedHandlerList<T> consumer(@Nonnull Consumer<? super T> handler) {
      Objects.requireNonNull(handler, "handler");
      return this.biConsumer(Delegates.consumerToBiConsumerSecond(handler));
   }

   @Nonnull
   MergedHandlerList<T> biConsumer(@Nonnull BiConsumer<MergedSubscription<T>, ? super T> biConsumer);
}
