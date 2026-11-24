package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.command.MMOItemsCommandTreeRoot;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommandTreeNode extends CommandTreeNode {
   public CreateCommandTreeNode(CommandTreeNode var1) {
      super(var1, "create");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(new Parameter("<id>", (var0, var1x) -> var1x.add("ITEM_ID")));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 3) {
         return CommandResult.THROW_USAGE;
      }

      if (!Type.isValid(var2[1])) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item type called " + var2[1].toUpperCase().replace("-", "_") + ".");
         var1.sendMessage(
            MMOItems.plugin.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type" + ChatColor.RED + " to see all the available item types."
         );
         return CommandResult.FAILURE;
      }

      Type var3 = Type.get(var2[1]);
      String var4 = var2[2].toUpperCase().replace("-", "_");
      ConfigFile var5 = var3.getConfigFile();
      if (var5.getConfig().contains(var4)) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is already an item called " + var4 + ".");
         return CommandResult.FAILURE;
      }

      var5.getConfig().set(var4 + ".base.material", var3.getItem().getType().name());
      var5.save();
      MMOItems.plugin.getTemplates().requestTemplateUpdate(var3, var4);
      if (var1 instanceof Player) {
         new ItemEdition((Player)var1, MMOItems.plugin.getTemplates().getTemplate(var3, var4)).open();
      }

      var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GREEN + "You successfully created " + var4 + "!");
      return CommandResult.SUCCESS;
   }
}
