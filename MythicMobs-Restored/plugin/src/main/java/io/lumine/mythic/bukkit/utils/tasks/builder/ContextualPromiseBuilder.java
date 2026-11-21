package io.lumine.mythic.bukkit.utils.tasks.builder;

import io.lumine.mythic.bukkit.utils.promise.Promise;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public interface ContextualPromiseBuilder {
   @Nonnull
   <T> Promise<T> supply(@Nonnull Supplier<T> supplier);

   @Nonnull
   <T> Promise<T> call(@Nonnull Callable<T> callable);

   @Nonnull
   Promise<Void> run(@Nonnull Runnable runnable);
}
