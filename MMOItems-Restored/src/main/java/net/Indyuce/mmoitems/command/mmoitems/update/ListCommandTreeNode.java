package net.Indyuce.mmoitems.command.mmoitems.update;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.PluginUpdate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommandTreeNode extends CommandTreeNode {
   public ListCommandTreeNode(CommandTreeNode var1) {
      super(var1, "list");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      var1.sendMessage(ChatColor.LIGHT_PURPLE + "Available Config Updates");

      for (PluginUpdate var4 : MMOItems.plugin.getUpdates().getAll()) {
         var1.sendMessage(ChatColor.DARK_GRAY + "- Update " + var4.getId());
      }

      return CommandResult.SUCCESS;
   }
}
