package io.lumine.mythic.lib.command.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommandTreeRoot extends CommandTreeNode implements CommandExecutor, TabCompleter {
   protected final String permission;
   private final String description;
   private final List<String> aliases = new ArrayList<>();

   public CommandTreeRoot(@NotNull String id, @NotNull String permission) {
      super(null, id);
      this.permission = permission;
      this.description = "No description provided";
   }

   public CommandTreeRoot(@Nullable ConfigurationSection config, @NotNull ToggleableCommand command) {
      super(null, config == null ? command.getName() : config.getString("main"));
      this.permission = config == null ? command.getPermission() : config.getString("permission", command.getPermission());
      this.description = config == null ? command.getDescription() : config.getString("description", command.getDescription());
      if (config != null) {
         this.aliases.addAll(config.getStringList("aliases"));
      }
   }

   @NotNull
   public BukkitCommand getCommand() {
      return new BukkitCommand(this.getId(), this.description, "", this.aliases) {
         @NotNull
         public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
            if (!sender.hasPermission(CommandTreeRoot.this.permission)) {
               return new ArrayList<>();
            }

            List<String> list = new CommandTreeExplorer(CommandTreeRoot.this, args).calculateTabCompletion();
            return args[args.length - 1].isEmpty()
               ? list
               : list.stream().filter(string -> string.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
         }

         public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
            if (!sender.hasPermission(CommandTreeRoot.this.permission)) {
               return false;
            }

            CommandTreeNode explorer = new CommandTreeExplorer(CommandTreeRoot.this, args).getNode();
            CommandTreeNode.CommandResult executionResult = explorer.execute(sender, args);
            Validate.notNull(executionResult, "Command execution result cannot be null");
            if (executionResult == CommandTreeNode.CommandResult.THROW_USAGE) {
               explorer.calculateUsageList().forEach(str -> sender.sendMessage(ChatColor.YELLOW + "/" + str));
            }

            return executionResult == CommandTreeNode.CommandResult.SUCCESS;
         }
      };
   }

   @Deprecated
   @Override
   public CommandTreeNode.CommandResult execute(CommandSender sender, String[] args) {
      return CommandTreeNode.CommandResult.THROW_USAGE;
   }

   @Deprecated
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission(this.permission)) {
         return false;
      }

      CommandTreeNode explorer = new CommandTreeExplorer(this, args).getNode();
      CommandTreeNode.CommandResult res = explorer.execute(sender, args);
      Validate.notNull(res, "Command result cannot be null");
      if (res == CommandTreeNode.CommandResult.THROW_USAGE) {
         explorer.calculateUsageList().forEach(str -> sender.sendMessage(ChatColor.YELLOW + "/" + str));
      }

      return res == CommandTreeNode.CommandResult.SUCCESS;
   }

   @Deprecated
   public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission(this.permission)) {
         return new ArrayList<>();
      }

      List<String> list = new CommandTreeExplorer(this, args).calculateTabCompletion();
      return args[args.length - 1].isEmpty()
         ? list
         : list.stream().filter(string -> string.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
   }
}
