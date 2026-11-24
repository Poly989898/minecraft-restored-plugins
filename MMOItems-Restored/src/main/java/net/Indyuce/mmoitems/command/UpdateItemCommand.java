package net.Indyuce.mmoitems.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpdateItemCommand implements CommandExecutor {
   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return true;
      }

      if (!var1.hasPermission("mmoitems.update")) {
         return true;
      }

      Player var5 = (Player)var1;
      if (var4.length >= 1 && !var5.hasPermission("mmoitems.admin")) {
      }

      return true;
   }
}
