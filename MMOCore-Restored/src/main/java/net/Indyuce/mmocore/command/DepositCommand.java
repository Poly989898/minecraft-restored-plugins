package net.Indyuce.mmocore.command;

import net.Indyuce.mmocore.command.api.RegisteredCommand;
import net.Indyuce.mmocore.command.api.ToggleableCommand;
import net.Indyuce.mmocore.gui.eco.DepositMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class DepositCommand extends RegisteredCommand {
   public DepositCommand(ConfigurationSection config) {
      super(config, ToggleableCommand.DEPOSIT);
   }

   public boolean execute(CommandSender sender, String label, String[] args) {
      if (!sender.hasPermission("mmocore.currency")) {
         return false;
      } else {
         Player player = args.length > 0 && sender.hasPermission("mmocore.admin")
            ? Bukkit.getPlayer(args[0])
            : (sender instanceof Player ? (Player)sender : null);
         if (player == null) {
            sender.sendMessage(ChatColor.RED + "Please specify a valid player.");
            return true;
         } else {
            new DepositMenu(player).open();
            return true;
         }
      }
   }
}
