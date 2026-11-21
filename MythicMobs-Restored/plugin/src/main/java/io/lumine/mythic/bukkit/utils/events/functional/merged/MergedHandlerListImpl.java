package io.lumine.mythic.bukkit.utils.events.functional.merged;

import io.lumine.mythic.bukkit.utils.events.MergedSubscription;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;

class MergedHandlerListImpl<T> implements MergedHandlerList<T> {
   private final MergedSubscriptionBuilderImpl<T> builder;
   private final List<BiConsumer<MergedSubscription<T>, ? super T>> handlers = new ArrayList<>(1);

   MergedHandlerListImpl(@Nonnull MergedSubscriptionBuilderImpl<T> builder) {
      this.builder = builder;
   }

   @Nonnull
   @Override
   public MergedHandlerList<T> biConsumer(@Nonnull BiConsumer<MergedSubscription<T>, ? super T> handler) {
      Objects.requireNonNull(handler, "handler");
      this.handlers.add(handler);
      return this;
   }

   @Nonnull
   public MergedSubscription<T> register() {
      if (this.handlers.isEmpty()) {
         throw new IllegalStateException("No handlers have been registered");
      } else {
         MergedEventListener<T> listener = new MergedEventListener<>(this.builder, this.handlers);
         listener.register(LoaderUtils.getPlugin());
         return listener;
      }
   }
}
