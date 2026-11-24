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
   public BrowseCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "browse");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else if (texts.length < 2) {
         new ItemBrowser((Player)sender).open();
         return CommandResult.SUCCESS;
      } else if (!Type.isValid(texts[1])) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Please specify a valid item type.");
         return CommandResult.FAILURE;
      } else {
         new ItemBrowser((Player)sender, Type.get(texts[1])).open();
         return CommandResult.SUCCESS;
      }
   }
}
