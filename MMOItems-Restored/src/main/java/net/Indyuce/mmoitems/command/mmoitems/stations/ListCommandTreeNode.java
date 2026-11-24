package net.Indyuce.mmoitems.command.mmoitems.stations;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.CraftingStation;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommandTreeNode extends CommandTreeNode {
   public ListCommandTreeNode(CommandTreeNode var1) {
      super(var1, "list");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      var1.sendMessage(
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

      for (CraftingStation var4 : MMOItems.plugin.getCrafting().getAll()) {
         var1.sendMessage(ChatColor.GRAY + "- " + ChatColor.WHITE + var4.getId());
      }

      return CommandResult.SUCCESS;
   }
}
