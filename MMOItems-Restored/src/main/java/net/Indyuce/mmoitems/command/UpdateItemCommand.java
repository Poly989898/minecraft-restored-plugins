package net.Indyuce.mmoitems.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpdateItemCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String text, String[] texts) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is only for players.");
         return true;
      }

      if (!sender.hasPermission("mmoitems.update")) {
         return true;
      }

      Player player = (Player)sender;
      if (texts.length >= 1 && !player.hasPermission("mmoitems.admin")) {
      }

      return true;
   }
}
