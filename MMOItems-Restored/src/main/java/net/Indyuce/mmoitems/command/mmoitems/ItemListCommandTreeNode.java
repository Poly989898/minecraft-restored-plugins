package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.command.MMOItemsCommandTreeRoot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ItemListCommandTreeNode extends CommandTreeNode {
   public ItemListCommandTreeNode(CommandTreeNode var1) {
      super(var1, "itemlist");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 2) {
         return CommandResult.THROW_USAGE;
      }

      if (!Type.isValid(var2[1])) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item type called " + var2[1].toUpperCase().replace("-", "_"));
         var1.sendMessage(MMOItems.plugin.getPrefix() + "Type " + ChatColor.GREEN + "/mi list type " + ChatColor.GRAY + "to see all the available item types.");
         return CommandResult.FAILURE;
      }

      Type var3 = Type.get(var2[1]);
      var1.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
      var1.sendMessage(ChatColor.GREEN + "List of all items in " + var3.getId().toLowerCase() + ".yml:");
      FileConfiguration var4 = var3.getConfigFile().getConfig();
      if (var1 instanceof Player) {
         for (String var6 : var4.getKeys(false)) {
            String var7 = var4.getConfigurationSection(var6).contains("name")
               ? " " + ChatColor.WHITE + "(" + MythicLib.plugin.parseColors(var4.getString(var6 + ".name")) + ChatColor.WHITE + ")"
               : "";
            MythicLib.plugin
               .getVersion()
               .getWrapper()
               .sendJson(
                  (Player)var1,
                  "{\"text\":\"* "
                     + ChatColor.GREEN
                     + var6
                     + var7
                     + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/mi edit "
                     + var3.getId()
                     + " "
                     + var6
                     + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Click to edit "
                     + (var7.equals("") ? var6 : MythicLib.plugin.parseColors(var4.getString(var6 + ".name")))
                     + ChatColor.WHITE
                     + ".\",\"color\":\"white\"}}}"
               );
         }
      } else {
         for (String var9 : var4.getKeys(false)) {
            var1.sendMessage(
               "* "
                  + ChatColor.GREEN
                  + var9
                  + (
                     var4.getConfigurationSection(var9).contains("name")
                        ? " " + ChatColor.WHITE + "(" + MythicLib.plugin.parseColors(var4.getString(var9 + ".name")) + ChatColor.WHITE + ")"
                        : ""
                  )
            );
         }
      }

      return CommandResult.SUCCESS;
   }
}
