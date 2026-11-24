package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckTagsCommandTreeNode extends CommandTreeNode {
   public CheckTagsCommandTreeNode(CommandTreeNode var1) {
      super(var1, "checktags");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      }

      Player var3 = (Player)var1;
      var3.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");

      for (String var5 : MythicLib.plugin.getVersion().getWrapper().getNBTItem(var3.getInventory().getItemInMainHand()).getTags()) {
         var3.sendMessage("- " + var5);
      }

      return CommandResult.SUCCESS;
   }
}
