package it.ytnoos.lpx.commands.sub;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {
   public ReloadCommand(LPXBukkit lpxBukkit2) {
      super(lpxBukkit2, "reload", "lpx.reload", "Reload configuration", false);
   }

   @Override
   public void execute(CommandSender commandSender, String[] strings) {
      this.lpxBukkit.reloadConfig();
      this.sendMessage(commandSender, ChatColor.GREEN + "Config reloaded!");
   }
}
