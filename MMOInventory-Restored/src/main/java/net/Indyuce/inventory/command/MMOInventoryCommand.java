package net.Indyuce.inventory.command;

import io.lumine.mythic.lib.data.DataExport;
import io.lumine.mythic.lib.data.sql.SQLDataSource;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.gui.PlayerInventoryView;
import net.Indyuce.inventory.manager.data.sql.SQLDataHandler;
import net.Indyuce.inventory.manager.data.yaml.YAMLDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MMOInventoryCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length < 1 && sender instanceof Player) {
         if (!sender.hasPermission("mmoinventory.open")) {
            sender.sendMessage(MMOInventory.plugin.getTranslation("not-enough-perms"));
            return true;
         } else {
            new PlayerInventoryView((Player)sender).open();
            return true;
         }
      } else {
         if (args.length == 0) {
            return true;
         }

         if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("mmoinventory.admin")) {
               sender.sendMessage(MMOInventory.plugin.getTranslation("not-enough-perms"));
               return true;
            } else {
               MMOInventory.plugin.reload();
               sender.sendMessage(MMOInventory.plugin.getTranslation("reload").replace("{version}", MMOInventory.plugin.getDescription().getVersion()));
               return true;
            }
         } else if (args[0].equalsIgnoreCase("export-data")) {
            if (!sender.hasPermission("mmoinventory.admin")) {
               sender.sendMessage(MMOInventory.plugin.getTranslation("not-enough-perms"));
               return true;
            } else {
               return new DataExport(MMOInventory.plugin.getDataManager(), sender)
                  .start(YAMLDataHandler::new, () -> new SQLDataHandler(new SQLDataSource(MMOInventory.plugin)));
            }
         } else if (!sender.hasPermission("mmoinventory.open.other")) {
            sender.sendMessage(MMOInventory.plugin.getTranslation("not-enough-perms"));
            return true;
         } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
               sender.sendMessage(MMOInventory.plugin.getTranslation("wrong-player").replace("{arg}", args[0]));
               return true;
            } else {
               new PlayerInventoryView((Player)sender, target).open();
               return true;
            }
         }
      }
   }
}
