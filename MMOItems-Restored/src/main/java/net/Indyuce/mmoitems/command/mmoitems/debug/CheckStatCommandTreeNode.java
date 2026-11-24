package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckStatCommandTreeNode extends CommandTreeNode {
   public CheckStatCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "checkstat");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length < 3) {
         return CommandResult.THROW_USAGE;
      } else if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else {
         ItemStat itemStat = MMOItems.plugin.getStats().get(texts[2].toUpperCase().replace("-", "_"));
         if (itemStat == null) {
            sender.sendMessage(ChatColor.RED + "Couldn't find the stat called " + texts[2].toUpperCase().replace("-", "_") + ".");
            return CommandResult.FAILURE;
         } else {
            Player player = (Player)sender;
            player.sendMessage("Found stat with ID " + itemStat.getId() + " = " + PlayerData.get((Player)sender).getStats().getStat(itemStat));
            return CommandResult.SUCCESS;
         }
      }
   }
}
