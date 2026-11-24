package net.Indyuce.mmoitems.command.mmoitems.revid;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import java.util.ArrayList;
import java.util.function.Function;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RevIDActionCommandTreeNode extends CommandTreeNode {
   private final String cmdType;
   private final Function<Integer, Integer> modifier;

   public RevIDActionCommandTreeNode(CommandTreeNode commandTreeNode, String text, Function<Integer, Integer> function) {
      super(commandTreeNode, text);
      this.cmdType = text;
      this.modifier = function;
      this.addParameter(RevisionIDCommandTreeNode.TYPE_OR_ALL);
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length < 3) {
         return CommandResult.THROW_USAGE;
      }

      if (!Type.isValid(texts[2]) && !texts[2].equalsIgnoreCase("all")) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item type called " + texts[2].toUpperCase().replace("-", "_") + ".");
         sender.sendMessage(
            MMOItems.plugin.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type" + ChatColor.RED + " to see all the available item types."
         );
         return CommandResult.FAILURE;
      }

      Type type = texts[2].equalsIgnoreCase("all") ? null : Type.get(texts[2]);
      ArrayList<MMOItemTemplate> mmoItemTemplates = new ArrayList<>(type == null ? MMOItems.plugin.getTemplates().collectTemplates() : MMOItems.plugin.getTemplates().getTemplates(type));
      int count = 0;

      for (MMOItemTemplate mmoItemTemplate : mmoItemTemplates) {
         ConfigFile configFile = mmoItemTemplate.getType().getConfigFile();
         if (!configFile.getConfig().isConfigurationSection(mmoItemTemplate.getId() + ".base")) {
            count++;
         } else {
            configFile.getConfig().getConfigurationSection(mmoItemTemplate.getId() + ".base").set("revision-id", this.modifier.apply(mmoItemTemplate.getRevisionId()));
            configFile.registerTemplateEdition(mmoItemTemplate);
         }
      }

      if (count > 0) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Couldn't find ConfigurationSection for " + count + " of the specified items.");
      } else {
         sender.sendMessage(
            MMOItems.plugin.getPrefix() + ChatColor.GREEN + "Successfully " + this.cmdType + "d Rev IDs" + (type != null ? " for " + type.getName() : "") + "!"
         );
      }

      return CommandResult.SUCCESS;
   }
}
