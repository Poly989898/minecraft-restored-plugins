package it.ytnoos.lpx.commands;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.commands.sub.AlertsCommand;
import it.ytnoos.lpx.commands.sub.GuiCommand;
import it.ytnoos.lpx.commands.sub.HealthCommand;
import it.ytnoos.lpx.commands.sub.KickCommand;
import it.ytnoos.lpx.commands.sub.MonitorCommand;
import it.ytnoos.lpx.commands.sub.ReloadCommand;
import it.ytnoos.lpx.commands.sub.WatchdogCommand;
import it.ytnoos.lpx.utils.string.MessageFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class LPXCommand implements CommandExecutor, TabCompleter {
   private static final boolean SUPPORTS_COMMAND_PERMISSION = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19_1);
   private final LPXBukkit lpxBukkit;
   private final PluginCommand lpx;
   private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();

   public LPXCommand(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
      this.lpx = ((JavaPlugin)lpxBukkit2.getPlugin()).getCommand("lpx");
      this.lpx.setExecutor(this);
      this.lpx.setTabCompleter(this);
      this.registerSubCommand(new ReloadCommand(lpxBukkit2));
      this.registerSubCommand(new GuiCommand(lpxBukkit2));
      this.registerSubCommand(new AlertsCommand(lpxBukkit2));
      this.registerSubCommand(new KickCommand(lpxBukkit2));
      this.registerSubCommand(new MonitorCommand(lpxBukkit2));
      this.registerSubCommand(new HealthCommand(lpxBukkit2));
      this.registerSubCommand(new WatchdogCommand(lpxBukkit2));
      this.updateHiddenCommand();
   }

   private void registerSubCommand(SubCommand subCommand) {
      this.subCommands.put(subCommand.getName().toLowerCase(Locale.ROOT), subCommand);
   }

   public boolean onCommand(CommandSender commandSender, Command command, String string, String[] strings) {
      if (!this.hasAnyPermission(commandSender)) {
         MessageFormatter.send(commandSender, this.getUnknownCommandMessage());
         return true;
      }

      boolean flag = this.hasAnyPermission2(commandSender);
      if (strings.length != 0) {
         SubCommand subCommand = this.getSubCommand(strings[0]);
         if (subCommand == null) {
            MessageFormatter.send(commandSender, this.lpxBukkit.getConfigManager().getMessagesConfig().getInvalidArguments());
            return true;
         } else if (!commandSender.hasPermission(subCommand.getPermission())) {
            MessageFormatter.send(commandSender, this.lpxBukkit.getConfigManager().getMessagesConfig().getNoPermission().replace("%permission%", subCommand.getPermission()));
            return true;
         } else if (subCommand.isPlayerOnly() && !(commandSender instanceof Player)) {
            MessageFormatter.send(
               commandSender, this.lpxBukkit.getConfigManager().getMessagesConfig().getPrefix() + ChatColor.RED + " You must be a player to execute this command!"
            );
            return true;
         } else {
            subCommand.execute(commandSender, strings);
            return true;
         }
      } else {
         String string2 = this.lpxBukkit.getConfigManager().getMessagesConfig().getMainColor();
         String string3 = this.lpxBukkit.getConfigManager().getMessagesConfig().getSecondColor();
         String string4 = this.lpxBukkit.getPlugin().getDescription().getVersion().replace('-', ' ');
         String string5 = string3;
         String string6 = string4;
         String string7 = string2;
         String string8 = string2 + "●<reset><gray> Running " + string7 + "<bold>LPX " + string6 + "<reset><gray> by <reset>" + string5 + "Ytnoos";
         MessageFormatter.send(commandSender, string8);
         if (flag) {
            commandSender.sendMessage("");
         }

         for (SubCommand subCommand2 : this.subCommands.values()) {
            if (commandSender.hasPermission(subCommand2.getPermission()) && (!subCommand2.isPlayerOnly() || commandSender instanceof Player)) {
               String string9 = subCommand2.getName();
               String string10 = subCommand2.getDescription();
               String string11 = subCommand2.getUsage();
               String string12 = string9;
               String string13 = string3;
               MessageFormatter.send(commandSender, string2 + "<bold>> <reset>" + string13 + "/lpx " + string12 + " " + string11 + " <reset><gray>- <reset><white>" + string10);
            }
         }

         return true;
      }
   }

   public List<String> onTabComplete(CommandSender commandSender, Command command, String string, String[] strings) {
      if (!this.hasAnyPermission(commandSender)) {
         return Collections.emptyList();
      }

      if (strings.length == 1) {
         ArrayList arrayList = new ArrayList();
         String string2 = strings[0];

         for (SubCommand subCommand : this.subCommands.values()) {
            if (this.canUse(commandSender, subCommand) && subCommand.getName().regionMatches(true, 0, string2, 0, string2.length())) {
               arrayList.add(subCommand.getName());
            }
         }

         return arrayList;
      } else {
         SubCommand subCommand2 = this.getSubCommand(strings[0]);
         return subCommand2 != null && this.canUse(commandSender, subCommand2) ? subCommand2.tabComplete(commandSender, strings) : Collections.emptyList();
      }
   }

   private String getUnknownCommandMessage() {
      String string;
      try {
         string = this.lpxBukkit.getPlugin().getServer().spigot().getSpigotConfig().getString("messages.unknown-command");
      } catch (Throwable throwable) {
         string = this.lpxBukkit.getConfigManager().getMessagesConfig().getUnknownCommand();
      }

      return ChatColor.translateAlternateColorCodes('&', string);
   }

   private SubCommand getSubCommand(String string) {
      return this.subCommands.get(string.toLowerCase(Locale.ROOT));
   }

   public boolean hasAnyPermission(CommandSender commandSender) {
      return !this.lpxBukkit.getConfigManager().getOptionsConfig().isHiddenCommand() || this.hasAnyPermission2(commandSender);
   }

   public void updateHiddenCommand() {
      boolean flag = this.lpxBukkit.getConfigManager().getOptionsConfig().isHiddenCommand() && SUPPORTS_COMMAND_PERMISSION;
      this.lpx.setPermission(flag ? String.join(";", this.getSubCommandPermissions()) : null);
      this.lpx.setPermissionMessage(flag ? "" : null);
   }

   private List getSubCommandPermissions() {
      ArrayList arrayList = new ArrayList();

      for (SubCommand subCommand : this.subCommands.values()) {
         if (!arrayList.contains(subCommand.getPermission())) {
            arrayList.add(subCommand.getPermission());
         }
      }

      return arrayList;
   }

   private boolean hasAnyPermission2(CommandSender commandSender) {
      for (SubCommand subCommand : this.subCommands.values()) {
         if (commandSender.hasPermission(subCommand.getPermission())) {
            return true;
         }
      }

      return false;
   }

   private boolean canUse(CommandSender commandSender, SubCommand subCommand) {
      return commandSender.hasPermission(subCommand.getPermission()) && (!subCommand.isPlayerOnly() || commandSender instanceof Player);
   }

}
