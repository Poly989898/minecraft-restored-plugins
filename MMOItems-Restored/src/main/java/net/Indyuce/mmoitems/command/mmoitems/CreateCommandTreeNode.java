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
   public CreateCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "create");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(new Parameter("<id>", (commandTreeExplorer, texts) -> texts.add("ITEM_ID")));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length < 3) {
         return CommandResult.THROW_USAGE;
      }

      if (!Type.isValid(texts[1])) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item type called " + texts[1].toUpperCase().replace("-", "_") + ".");
         sender.sendMessage(
            MMOItems.plugin.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type" + ChatColor.RED + " to see all the available item types."
         );
         return CommandResult.FAILURE;
      }

      Type type = Type.get(texts[1]);
      String text = texts[2].toUpperCase().replace("-", "_");
      ConfigFile configFile = type.getConfigFile();
      if (configFile.getConfig().contains(text)) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is already an item called " + text + ".");
         return CommandResult.FAILURE;
      }

      configFile.getConfig().set(text + ".base.material", type.getItem().getType().name());
      configFile.save();
      MMOItems.plugin.getTemplates().requestTemplateUpdate(type, text);
      if (sender instanceof Player) {
         new ItemEdition((Player)sender, MMOItems.plugin.getTemplates().getTemplate(type, text)).open();
      }

      sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GREEN + "You successfully created " + text + "!");
      return CommandResult.SUCCESS;
   }
}
