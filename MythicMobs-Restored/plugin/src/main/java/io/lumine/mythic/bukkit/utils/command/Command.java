package io.lumine.mythic.bukkit.utils.command;

import io.lumine.mythic.bukkit.utils.command.context.CommandContext;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import java.util.List;
import javax.annotation.Nonnull;

public interface Command extends Terminable {
   void register(@Nonnull String... strs);

   default void registerAndBind(@Nonnull TerminableConsumer consumer, @Nonnull String... aliases) {
      this.register(aliases);
      this.bindWith(consumer);
   }

   void call(@Nonnull CommandContext<?> commandContext) throws CommandInterruptException;

   List<String> callTabCompleter(@Nonnull CommandContext<?> commandContext) throws CommandInterruptException;
}
