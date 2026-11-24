package net.Indyuce.mmoitems.command.mmoitems.update;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.PluginUpdate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class InfoCommandTreeNode extends CommandTreeNode {
   public InfoCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "info");
      this.addParameter(new Parameter("<id>", (commandTreeExplorer, var1x) -> MMOItems.plugin.getUpdates().getAll().forEach(var1xx -> var1x.add("" + var1xx.getId()))));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length < 3) {
         return CommandResult.THROW_USAGE;
      }

      int index;
      try {
         index = Integer.parseInt(texts[2]);
      } catch (NumberFormatException exception) {
         sender.sendMessage(ChatColor.RED + "Please specify a valid number.");
         return CommandResult.FAILURE;
      }

      if (!MMOItems.plugin.getUpdates().has(index)) {
         sender.sendMessage(ChatColor.RED + "Could not find any config update with ID " + index);
         return CommandResult.FAILURE;
      }

      PluginUpdate pluginUpdate = MMOItems.plugin.getUpdates().get(index);
      sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Config Update n" + pluginUpdate.getId());
      if (pluginUpdate.hasDescription()) {
         sender.sendMessage("");
         sender.sendMessage(ChatColor.DARK_GRAY + "Description:");

         for (String text : pluginUpdate.getDescription()) {
            sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.translateAlternateColorCodes('&', text));
         }
      }

      sender.sendMessage("");
      sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GOLD + "/mi update apply " + pluginUpdate.getId() + ChatColor.YELLOW + " to apply this config update.");
      return CommandResult.SUCCESS;
   }
}
