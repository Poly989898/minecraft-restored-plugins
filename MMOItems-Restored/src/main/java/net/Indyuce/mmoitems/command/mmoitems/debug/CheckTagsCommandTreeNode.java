package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckTagsCommandTreeNode extends CommandTreeNode {
   public CheckTagsCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "checktags");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      }

      Player player = (Player)sender;
      player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");

      for (String tag : MythicLib.plugin.getVersion().getWrapper().getNBTItem(player.getInventory().getItemInMainHand()).getTags()) {
         player.sendMessage("- " + tag);
      }

      return CommandResult.SUCCESS;
   }
}
