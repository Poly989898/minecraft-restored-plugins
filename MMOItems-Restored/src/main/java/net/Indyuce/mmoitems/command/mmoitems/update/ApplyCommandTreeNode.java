package net.Indyuce.mmoitems.command.mmoitems.update;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.PluginUpdate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ApplyCommandTreeNode extends CommandTreeNode {
   public ApplyCommandTreeNode(CommandTreeNode var1) {
      super(var1, "apply");
      this.addParameter(new Parameter("<id>", (var0, var1x) -> MMOItems.plugin.getUpdates().getAll().forEach(var1xx -> var1x.add("" + var1xx.getId()))));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 3) {
         return CommandResult.THROW_USAGE;
      }

      int var3;
      try {
         var3 = Integer.parseInt(var2[2]);
      } catch (NumberFormatException var5) {
         var1.sendMessage(ChatColor.RED + "Please specify a valid number.");
         return CommandResult.FAILURE;
      }

      if (!MMOItems.plugin.getUpdates().has(var3)) {
         var1.sendMessage(ChatColor.RED + "Could not find any config update with ID " + var3);
         return CommandResult.FAILURE;
      } else {
         PluginUpdate var4 = MMOItems.plugin.getUpdates().get(var3);
         var1.sendMessage(ChatColor.YELLOW + "Applying config update " + var3 + "...");
         var4.apply(var1);
         var1.sendMessage(ChatColor.YELLOW + "Config update " + var3 + " was successfully applied. Check the console for potential update error logs.");
         return CommandResult.SUCCESS;
      }
   }
}
