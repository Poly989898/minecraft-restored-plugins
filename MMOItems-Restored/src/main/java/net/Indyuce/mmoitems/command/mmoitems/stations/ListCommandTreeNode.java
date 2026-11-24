package net.Indyuce.mmoitems.command.mmoitems.stations;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.CraftingStation;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommandTreeNode extends CommandTreeNode {
   public ListCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "list");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      sender.sendMessage(
         ChatColor.DARK_GRAY
            + ""
            + ChatColor.STRIKETHROUGH
            + "-----------------["
            + ChatColor.LIGHT_PURPLE
            + " Crafting Stations "
            + ChatColor.DARK_GRAY
            + ""
            + ChatColor.STRIKETHROUGH
            + "]-----------------"
      );

      for (CraftingStation craftingStation : MMOItems.plugin.getCrafting().getAll()) {
         sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.WHITE + craftingStation.getId());
      }

      return CommandResult.SUCCESS;
   }
}
