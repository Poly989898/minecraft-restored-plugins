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

   public RevIDActionCommandTreeNode(CommandTreeNode var1, String var2, Function<Integer, Integer> var3) {
      super(var1, var2);
      this.cmdType = var2;
      this.modifier = var3;
      this.addParameter(RevisionIDCommandTreeNode.TYPE_OR_ALL);
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 3) {
         return CommandResult.THROW_USAGE;
      }

      if (!Type.isValid(var2[2]) && !var2[2].equalsIgnoreCase("all")) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item type called " + var2[2].toUpperCase().replace("-", "_") + ".");
         var1.sendMessage(
            MMOItems.plugin.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type" + ChatColor.RED + " to see all the available item types."
         );
         return CommandResult.FAILURE;
      }

      Type var3 = var2[2].equalsIgnoreCase("all") ? null : Type.get(var2[2]);
      ArrayList<MMOItemTemplate> var4 = new ArrayList<>(var3 == null ? MMOItems.plugin.getTemplates().collectTemplates() : MMOItems.plugin.getTemplates().getTemplates(var3));
      int var5 = 0;

      for (MMOItemTemplate var7 : var4) {
         ConfigFile var8 = var7.getType().getConfigFile();
         if (!var8.getConfig().isConfigurationSection(var7.getId() + ".base")) {
            var5++;
         } else {
            var8.getConfig().getConfigurationSection(var7.getId() + ".base").set("revision-id", this.modifier.apply(var7.getRevisionId()));
            var8.registerTemplateEdition(var7);
         }
      }

      if (var5 > 0) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Couldn't find ConfigurationSection for " + var5 + " of the specified items.");
      } else {
         var1.sendMessage(
            MMOItems.plugin.getPrefix() + ChatColor.GREEN + "Successfully " + this.cmdType + "d Rev IDs" + (var3 != null ? " for " + var3.getName() : "") + "!"
         );
      }

      return CommandResult.SUCCESS;
   }
}
