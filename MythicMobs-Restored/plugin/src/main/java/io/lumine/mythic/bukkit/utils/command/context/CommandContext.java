package io.lumine.mythic.bukkit.utils.command.context;

import com.google.common.collect.ImmutableList;
import io.lumine.mythic.bukkit.utils.Players;
import io.lumine.mythic.bukkit.utils.command.argument.Argument;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.command.CommandSender;

public interface CommandContext<T extends CommandSender> {
   @Nonnull
   T sender();

   default void reply(String... message) {
      Players.msg(this.sender(), message);
   }

   @Nonnull
   ImmutableList<String> args();

   @Nonnull
   Argument arg(int value);

   @Nullable
   String rawArg(int value);

   @Nonnull
   String label();

   @Nonnull
   List<String> aliases();
}
