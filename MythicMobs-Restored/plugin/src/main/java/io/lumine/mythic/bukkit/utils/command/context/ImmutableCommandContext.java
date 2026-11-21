package io.lumine.mythic.bukkit.utils.command.context;

import com.google.common.collect.ImmutableList;
import io.lumine.mythic.bukkit.utils.command.argument.Argument;
import io.lumine.mythic.bukkit.utils.command.argument.SimpleArgument;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.command.CommandSender;

public class ImmutableCommandContext<T extends CommandSender> implements CommandContext<T> {
   private final T sender;
   private final String label;
   private final ImmutableList<String> args;
   private final ImmutableList<String> aliases;

   public ImmutableCommandContext(T sender, String label, String[] args, List<String> aliases) {
      this.sender = sender;
      this.label = label;
      this.args = ImmutableList.copyOf(args);
      this.aliases = ImmutableList.copyOf(aliases);
   }

   @Nonnull
   @Override
   public T sender() {
      return this.sender;
   }

   @Nonnull
   @Override
   public ImmutableList<String> args() {
      return this.args;
   }

   @Nonnull
   @Override
   public Argument arg(int index) {
      return new SimpleArgument(index, this.rawArg(index));
   }

   @Nullable
   @Override
   public String rawArg(int index) {
      return index >= 0 && index < this.args.size() ? this.args.get(index) : null;
   }

   @Nonnull
   @Override
   public String label() {
      return this.label;
   }

   @Nonnull
   public ImmutableList<String> aliases() {
      return this.aliases;
   }
}
