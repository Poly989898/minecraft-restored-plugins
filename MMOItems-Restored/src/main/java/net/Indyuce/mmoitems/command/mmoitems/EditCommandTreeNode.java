package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.command.MMOItemsCommandTreeRoot;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditCommandTreeNode extends CommandTreeNode {
   public EditCommandTreeNode(CommandTreeNode var1) {
      super(var1, "edit");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(MMOItemsCommandTreeRoot.ID_2);
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 3) {
         return CommandResult.THROW_USAGE;
      } else if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else if (!Type.isValid(var2[1])) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item type called " + var2[1].toUpperCase().replace("-", "_") + ".");
         var1.sendMessage(
            MMOItems.plugin.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type" + ChatColor.RED + " to see all the available item types."
         );
         return CommandResult.FAILURE;
      } else {
         Type var3 = Type.get(var2[1]);
         String var4 = var2[2].toUpperCase().replace("-", "_");
         if (!MMOItems.plugin.getTemplates().hasTemplate(var3, var4)) {
            var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Could not find a template called '" + var4 + "'.");
            return CommandResult.FAILURE;
         } else {
            new ItemEdition((Player)var1, MMOItems.plugin.getTemplates().getTemplate(var3, var4)).open();
            return CommandResult.SUCCESS;
         }
      }
   }
}
