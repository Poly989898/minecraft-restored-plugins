package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class AllItemsCommandTreeNode extends CommandTreeNode {
   public AllItemsCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "allitems");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
      sender.sendMessage(ChatColor.GREEN + "List of all MMOItems:");

      for (Type type : MMOItems.plugin.getTypes().getAll()) {
         FileConfiguration config = type.getConfigFile().getConfig();

         for (String key : config.getKeys(false)) {
            sender.sendMessage(
               "* "
                  + ChatColor.GREEN
                  + key
                  + (
                     config.getConfigurationSection(key).contains("name")
                        ? " " + ChatColor.WHITE + "(" + MythicLib.plugin.parseColors(config.getString(key + ".name")) + ChatColor.WHITE + ")"
                        : ""
                  )
            );
         }
      }

      return CommandResult.SUCCESS;
   }
}
