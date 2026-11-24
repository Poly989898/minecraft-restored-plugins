package net.Indyuce.mmoitems.command.mmoitems.update;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.PluginUpdate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ApplyCommandTreeNode extends CommandTreeNode {
   public ApplyCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "apply");
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
      } else {
         PluginUpdate pluginUpdate = MMOItems.plugin.getUpdates().get(index);
         sender.sendMessage(ChatColor.YELLOW + "Applying config update " + index + "...");
         pluginUpdate.apply(sender);
         sender.sendMessage(ChatColor.YELLOW + "Config update " + index + " was successfully applied. Check the console for potential update error logs.");
         return CommandResult.SUCCESS;
      }
   }
}
