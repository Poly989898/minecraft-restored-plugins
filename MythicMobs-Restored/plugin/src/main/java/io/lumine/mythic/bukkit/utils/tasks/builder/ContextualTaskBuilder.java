package io.lumine.mythic.bukkit.utils.tasks.builder;

import io.lumine.mythic.bukkit.utils.tasks.Task;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public interface ContextualTaskBuilder {
   @Nonnull
   Task consume(@Nonnull Consumer<Task> consumer);

   @Nonnull
   Task run(@Nonnull Runnable runnable);
}
