package io.lumine.mythic.bukkit.utils.terminable;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface TerminableConsumer {
   @Nonnull
   <T extends AutoCloseable> T bind(@Nonnull T t);

   @Nonnull
   default <T extends TerminableModule> T bindModule(@Nonnull T module) {
      module.setup(this);
      return module;
   }
}
