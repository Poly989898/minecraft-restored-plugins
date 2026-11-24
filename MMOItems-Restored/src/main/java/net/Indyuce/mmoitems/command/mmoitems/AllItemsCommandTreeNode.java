package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class AllItemsCommandTreeNode extends CommandTreeNode {
   public AllItemsCommandTreeNode(CommandTreeNode var1) {
      super(var1, "allitems");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      var1.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
      var1.sendMessage(ChatColor.GREEN + "List of all MMOItems:");

      for (Type var4 : MMOItems.plugin.getTypes().getAll()) {
         FileConfiguration var5 = var4.getConfigFile().getConfig();

         for (String var7 : var5.getKeys(false)) {
            var1.sendMessage(
               "* "
                  + ChatColor.GREEN
                  + var7
                  + (
                     var5.getConfigurationSection(var7).contains("name")
                        ? " " + ChatColor.WHITE + "(" + MythicLib.plugin.parseColors(var5.getString(var7 + ".name")) + ChatColor.WHITE + ")"
                        : ""
                  )
            );
         }
      }

      return CommandResult.SUCCESS;
   }
}
