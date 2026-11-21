package io.lumine.mythic.bukkit.utils.events.functional;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public interface FunctionalHandlerList<T, R> {
   @Nonnull
   FunctionalHandlerList<T, R> consumer(@Nonnull Consumer<? super T> consumer);

   @Nonnull
   FunctionalHandlerList<T, R> biConsumer(@Nonnull BiConsumer<R, ? super T> biConsumer);

   @Nonnull
   R register();
}
