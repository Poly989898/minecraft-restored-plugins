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
   public CopyCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "copy");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(MMOItemsCommandTreeRoot.ID_2);
      this.addParameter(new Parameter("<new-id>", (commandTreeExplorer, texts) -> {}));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length < 4) {
         return CommandResult.THROW_USAGE;
      }

      if (!Type.isValid(texts[1])) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item type called " + texts[1].toUpperCase().replace("-", "_") + ".");
         sender.sendMessage(MMOItems.plugin.getPrefix() + "Type " + ChatColor.GREEN + "/mi list type " + ChatColor.GRAY + "to see all the available item types.");
         return CommandResult.FAILURE;
      }

      Type type = Type.get(texts[1]);
      ConfigFile configFile = type.getConfigFile();
      String text = texts[2].toUpperCase().replace("-", "_");
      if (!configFile.getConfig().contains(text)) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item called " + text + ".");
         return CommandResult.FAILURE;
      }

      String targetId = texts[3].toUpperCase();
      if (configFile.getConfig().contains(targetId)) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is already an item called " + targetId + "!");
         return CommandResult.FAILURE;
      }

      configFile.getConfig().set(targetId, configFile.getConfig().getConfigurationSection(text));
      configFile.save();
      MMOItems.plugin.getTemplates().requestTemplateUpdate(type, targetId);
      if (sender instanceof Player) {
         new ItemEdition((Player)sender, MMOItems.plugin.getTemplates().getTemplate(type, targetId)).open();
      }

      sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GREEN + "You successfully copied " + text + " to " + targetId + "!");
      return CommandResult.SUCCESS;
   }
}
