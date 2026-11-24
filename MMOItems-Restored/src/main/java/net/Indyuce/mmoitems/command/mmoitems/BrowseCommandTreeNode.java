package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.command.MMOItemsCommandTreeRoot;
import net.Indyuce.mmoitems.gui.ItemBrowser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BrowseCommandTreeNode extends CommandTreeNode {
   public BrowseCommandTreeNode(CommandTreeNode var1) {
      super(var1, "browse");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else if (var2.length < 2) {
         new ItemBrowser((Player)var1).open();
         return CommandResult.SUCCESS;
      } else if (!Type.isValid(var2[1])) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Please specify a valid item type.");
         return CommandResult.FAILURE;
      } else {
         new ItemBrowser((Player)var1, Type.get(var2[1])).open();
         return CommandResult.SUCCESS;
      }
   }
}
