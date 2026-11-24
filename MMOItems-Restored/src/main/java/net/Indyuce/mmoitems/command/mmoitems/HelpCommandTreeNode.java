package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.command.PluginHelp;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommandTreeNode extends CommandTreeNode {
   public HelpCommandTreeNode(CommandTreeNode var1) {
      super(var1, "help");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 2) {
         new PluginHelp(var1).open(1);
         return CommandResult.SUCCESS;
      }

      try {
         new PluginHelp(var1).open(Integer.parseInt(var2[1]));
         return CommandResult.SUCCESS;
      } catch (NumberFormatException var4) {
         var1.sendMessage(ChatColor.RED + var2[1] + " is not a valid number.");
         return CommandResult.FAILURE;
      }
   }
}
