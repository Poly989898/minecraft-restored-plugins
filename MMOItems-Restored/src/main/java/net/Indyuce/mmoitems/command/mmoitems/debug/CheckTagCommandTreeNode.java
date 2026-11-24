package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckTagCommandTreeNode extends CommandTreeNode {
   public CheckTagCommandTreeNode(CommandTreeNode var1) {
      super(var1, "checktag");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 3) {
         return CommandResult.THROW_USAGE;
      } else if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else {
         Player var3 = (Player)var1;
         NBTItem var4 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var3.getInventory().getItemInMainHand());
         String var5 = var4.hasTag(var2[2]) ? var2[2] : "MMOITEMS_" + var2[2].toUpperCase().replace("-", "_");
         var3.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");
         var3.sendMessage(ChatColor.AQUA + "Boolean = " + ChatColor.RESET + var4.getBoolean(var5));
         var3.sendMessage(ChatColor.AQUA + "Double = " + ChatColor.RESET + var4.getDouble(var5));
         var3.sendMessage(ChatColor.AQUA + "String = " + ChatColor.RESET + var4.getString(var5));
         return CommandResult.SUCCESS;
      }
   }
}
