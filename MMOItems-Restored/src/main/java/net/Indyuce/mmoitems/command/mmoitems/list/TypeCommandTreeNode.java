package net.Indyuce.mmoitems.command.mmoitems.list;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TypeCommandTreeNode extends CommandTreeNode {
   public TypeCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "type");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      sender.sendMessage(
         ChatColor.DARK_GRAY
            + ""
            + ChatColor.STRIKETHROUGH
            + "-----------------["
            + ChatColor.LIGHT_PURPLE
            + " Item Types "
            + ChatColor.DARK_GRAY
            + ""
            + ChatColor.STRIKETHROUGH
            + ChatColor.LIGHT_PURPLE
            + " Item Types "
            + ChatColor.DARK_GRAY
            + ""
            + ChatColor.STRIKETHROUGH
            + "]-----------------"
      );

      for (Type type : MMOItems.plugin.getTypes().getAll()) {
         sender.sendMessage("* " + ChatColor.LIGHT_PURPLE + type.getName() + " (" + type.getId() + ")");
      }

      return CommandResult.SUCCESS;
   }
}
