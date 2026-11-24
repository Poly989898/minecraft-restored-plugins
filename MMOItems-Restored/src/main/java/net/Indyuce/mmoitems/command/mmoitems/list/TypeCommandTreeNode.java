package net.Indyuce.mmoitems.command.mmoitems.list;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TypeCommandTreeNode extends CommandTreeNode {
   public TypeCommandTreeNode(CommandTreeNode var1) {
      super(var1, "type");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      var1.sendMessage(
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

      for (Type var4 : MMOItems.plugin.getTypes().getAll()) {
         var1.sendMessage("* " + ChatColor.LIGHT_PURPLE + var4.getName() + " (" + var4.getId() + ")");
      }

      return CommandResult.SUCCESS;
   }
}
