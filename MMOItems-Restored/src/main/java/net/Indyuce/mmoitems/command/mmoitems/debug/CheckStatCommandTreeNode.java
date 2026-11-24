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
   public CheckStatCommandTreeNode(CommandTreeNode var1) {
      super(var1, "checkstat");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 3) {
         return CommandResult.THROW_USAGE;
      } else if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else {
         ItemStat var3 = MMOItems.plugin.getStats().get(var2[2].toUpperCase().replace("-", "_"));
         if (var3 == null) {
            var1.sendMessage(ChatColor.RED + "Couldn't find the stat called " + var2[2].toUpperCase().replace("-", "_") + ".");
            return CommandResult.FAILURE;
         } else {
            Player var4 = (Player)var1;
            var4.sendMessage("Found stat with ID " + var3.getId() + " = " + PlayerData.get((Player)var1).getStats().getStat(var3));
            return CommandResult.SUCCESS;
         }
      }
   }
}
