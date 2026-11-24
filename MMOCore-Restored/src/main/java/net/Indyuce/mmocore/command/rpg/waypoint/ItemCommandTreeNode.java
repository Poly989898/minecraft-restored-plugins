package net.Indyuce.mmocore.command.rpg.waypoint;

import io.lumine.mythic.lib.api.util.SmartGive;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.util.item.WaypointBookBuilder;
import net.Indyuce.mmocore.waypoint.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCommandTreeNode extends CommandTreeNode {
   public ItemCommandTreeNode(CommandTreeNode parent) {
      super(parent, "item");
      this.addParameter(new Parameter("<waypoint>", (explorer, list) -> MMOCore.plugin.waypointManager.getAll().forEach(way -> list.add(way.getId()))));
      this.addParameter(Parameter.PLAYER);
   }

   public CommandResult execute(CommandSender sender, String[] args) {
      if (args.length < 4) {
         return CommandResult.THROW_USAGE;
      } else if (!MMOCore.plugin.waypointManager.has(args[2])) {
         sender.sendMessage(ChatColor.RED + "Could not find waypoint " + args[2]);
         return CommandResult.FAILURE;
      } else {
         Player player = Bukkit.getPlayer(args[3]);
         if (player == null) {
            sender.sendMessage(ChatColor.RED + "Could not find player " + args[3]);
            return CommandResult.FAILURE;
         } else {
            Waypoint waypoint = MMOCore.plugin.waypointManager.get(args[2]);
            new SmartGive(player).give(new ItemStack[]{new WaypointBookBuilder(waypoint).build()});
            sender.sendMessage(
               ChatColor.GOLD
                  + "Gave "
                  + player.getName()
                  + ChatColor.YELLOW
                  + " a waypoint book of "
                  + ChatColor.GOLD
                  + waypoint.getId()
                  + ChatColor.YELLOW
                  + "."
            );
            return CommandResult.SUCCESS;
         }
      }
   }
}
