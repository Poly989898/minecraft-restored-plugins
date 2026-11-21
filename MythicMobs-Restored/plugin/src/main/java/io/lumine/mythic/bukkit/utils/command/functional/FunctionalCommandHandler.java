package io.lumine.mythic.bukkit.utils.command.functional;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.command.CommandInterruptException;
import io.lumine.mythic.bukkit.utils.command.context.CommandContext;
import org.bukkit.command.CommandSender;

@FunctionalInterface
@NonnullByDefault
public interface FunctionalCommandHandler<T extends CommandSender> {
   void handle(CommandContext<T> commandContext) throws CommandInterruptException;
}
