package net.Indyuce.mmoitems.command.mmoitems.update;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.PluginUpdate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommandTreeNode extends CommandTreeNode {
   public ListCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "list");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      sender.sendMessage(ChatColor.LIGHT_PURPLE + "Available Config Updates");

      for (PluginUpdate pluginUpdate : MMOItems.plugin.getUpdates().getAll()) {
         sender.sendMessage(ChatColor.DARK_GRAY + "- Update " + pluginUpdate.getId());
      }

      return CommandResult.SUCCESS;
   }
}
