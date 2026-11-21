package io.lumine.mythic.bukkit.utils.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

public abstract class Command<T extends Plugin> implements TabExecutor {
   protected final T plugin;
   private final Map<String, Command<T>> subCommands = new HashMap<>();
   private final Map<String, Command<T>> subCommandAliases = new HashMap<>();

   public Command(Command<T> parent) {
      this(parent.getPlugin());
   }

   public Command(T plugin) {
      this.plugin = plugin;
   }

   @SafeVarargs
   public final void addSubCommands(Command<T>... commands) {
      for (Command<T> command : commands) {
         this.subCommands.put(command.getName(), command);

         for (String alias : command.getAliases()) {
            this.subCommandAliases.put(alias, command);
         }
      }
   }

   public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
      if (this.getPermissionNode() != null && !sender.hasPermission(this.getPermissionNode()) && !sender.hasPermission("mythicmobs.admin")) {
         sender.sendMessage(ChatColor.RED + "You don't have permission to do this!");
         return true;
      } else if (!this.isConsoleFriendly() && !(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "Only players can do this!");
         return true;
      } else if (args.length > 0 && this.subCommands.get(args[0].toLowerCase()) != null) {
         Command<T> sub = this.subCommands.get(args[0].toLowerCase());
         return sub.onCommand(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
      } else if (args.length > 0 && this.subCommandAliases.get(args[0].toLowerCase()) != null) {
         Command<T> sub = this.subCommandAliases.get(args[0].toLowerCase());
         return sub.onCommand(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
      } else {
         return this.onCommand(sender, args);
      }
   }

   public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
      if (this.getPermissionNode() != null && !sender.hasPermission(this.getPermissionNode())) {
         return null;
      } else if (args.length > 1 && this.subCommands.get(args[0].toLowerCase()) != null) {
         Command<T> sub = this.subCommands.get(args[0].toLowerCase());
         return sub.onTabComplete(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
      } else if (args.length > 1 && this.subCommandAliases.get(args[0].toLowerCase()) != null) {
         Command<T> sub = this.subCommandAliases.get(args[0].toLowerCase());
         return sub.onTabComplete(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
      } else {
         List<String> result = this.onTabComplete(sender, args);
         if (result == null && args.length == 1) {
            result = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], this.subCommands.keySet(), result);
         }

         return result;
      }
   }

   public abstract boolean onCommand(CommandSender commandSender, String[] strs);

   public abstract List<String> onTabComplete(CommandSender commandSender, String[] strs);

   public abstract String getPermissionNode();

   public abstract boolean isConsoleFriendly();

   public String[] getAliases() {
      return new String[0];
   }

   public abstract String getName();

   protected T getPlugin() {
      return this.plugin;
   }
}
