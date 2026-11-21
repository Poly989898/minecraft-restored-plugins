package io.lumine.mythic.bukkit.utils.command.functional;

import io.lumine.mythic.bukkit.utils.command.CommandInterruptException;
import io.lumine.mythic.bukkit.utils.command.context.CommandContext;
import java.util.List;
import javax.annotation.Nullable;
import org.bukkit.command.CommandSender;

public interface FunctionalTabHandler<T extends CommandSender> {
   @Nullable
   List<String> handle(CommandContext<T> commandContext) throws CommandInterruptException;
}
