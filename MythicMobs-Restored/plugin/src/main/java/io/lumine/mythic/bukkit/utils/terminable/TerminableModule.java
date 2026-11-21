package io.lumine.mythic.bukkit.utils.terminable;

import javax.annotation.Nonnull;

public interface TerminableModule {
   void setup(@Nonnull TerminableConsumer terminableConsumer);

   default void bindModuleWith(@Nonnull TerminableConsumer consumer) {
      consumer.bindModule(this);
   }
}
