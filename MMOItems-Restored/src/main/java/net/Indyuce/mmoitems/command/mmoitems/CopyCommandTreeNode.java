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

public class CopyCommandTreeNode extends CommandTreeNode {
   public CopyCommandTreeNode(CommandTreeNode var1) {
      super(var1, "copy");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(MMOItemsCommandTreeRoot.ID_2);
      this.addParameter(new Parameter("<new-id>", (var0, var1x) -> {}));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 4) {
         return CommandResult.THROW_USAGE;
      }

      if (!Type.isValid(var2[1])) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item type called " + var2[1].toUpperCase().replace("-", "_") + ".");
         var1.sendMessage(MMOItems.plugin.getPrefix() + "Type " + ChatColor.GREEN + "/mi list type " + ChatColor.GRAY + "to see all the available item types.");
         return CommandResult.FAILURE;
      }

      Type var3 = Type.get(var2[1]);
      ConfigFile var4 = var3.getConfigFile();
      String var5 = var2[2].toUpperCase().replace("-", "_");
      if (!var4.getConfig().contains(var5)) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item called " + var5 + ".");
         return CommandResult.FAILURE;
      }

      String var6 = var2[3].toUpperCase();
      if (var4.getConfig().contains(var6)) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is already an item called " + var6 + "!");
         return CommandResult.FAILURE;
      }

      var4.getConfig().set(var6, var4.getConfig().getConfigurationSection(var5));
      var4.save();
      MMOItems.plugin.getTemplates().requestTemplateUpdate(var3, var6);
      if (var1 instanceof Player) {
         new ItemEdition((Player)var1, MMOItems.plugin.getTemplates().getTemplate(var3, var6)).open();
      }

      var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GREEN + "You successfully copied " + var5 + " to " + var6 + "!");
      return CommandResult.SUCCESS;
   }
}
