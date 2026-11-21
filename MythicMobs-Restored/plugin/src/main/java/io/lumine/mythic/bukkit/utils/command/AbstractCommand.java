package io.lumine.mythic.bukkit.utils.command;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.command.context.CommandContext;
import io.lumine.mythic.bukkit.utils.command.context.ImmutableCommandContext;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import java.util.List;
import javax.annotation.Nullable;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

@NonnullByDefault
public abstract class AbstractCommand implements Command, CommandExecutor, TabCompleter {
   @Nullable
   protected String permission;
   @Nullable
   protected String permissionMessage;
   @Nullable
   protected String description;

   @Override
   public void register(String... aliases) {
      LoaderUtils.getPlugin().registerCommand(this, this.permission, this.permissionMessage, this.description, aliases);
   }

   @Override
   public void close() {
      CommandMapUtil.unregisterCommand(this);
   }

   public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
      CommandContext<CommandSender> context = new ImmutableCommandContext<>(sender, label, args, command.getAliases());

      try {
         this.call(context);
      } catch (CommandInterruptException e) {
         e.getAction().accept(context.sender());
      }

      return true;
   }

   public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
      CommandContext<CommandSender> context = new ImmutableCommandContext<>(sender, label, args, command.getAliases());

      try {
         return this.callTabCompleter(context);
      } catch (CommandInterruptException e) {
         e.getAction().accept(context.sender());
         return null;
      }
   }
}
