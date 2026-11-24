package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckTagCommandTreeNode extends CommandTreeNode {
   public CheckTagCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "checktag");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length < 3) {
         return CommandResult.THROW_USAGE;
      } else if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else {
         Player player = (Player)sender;
         NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(player.getInventory().getItemInMainHand());
         String text = nBTItem.hasTag(texts[2]) ? texts[2] : "MMOITEMS_" + texts[2].toUpperCase().replace("-", "_");
         player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");
         player.sendMessage(ChatColor.AQUA + "Boolean = " + ChatColor.RESET + nBTItem.getBoolean(text));
         player.sendMessage(ChatColor.AQUA + "Double = " + ChatColor.RESET + nBTItem.getDouble(text));
         player.sendMessage(ChatColor.AQUA + "String = " + ChatColor.RESET + nBTItem.getString(text));
         return CommandResult.SUCCESS;
      }
   }
}
