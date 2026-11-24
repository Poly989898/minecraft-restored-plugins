package net.Indyuce.mmocore.command.rpg.waypoint;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.command.api.CommandVerbose;
import net.Indyuce.mmocore.waypoint.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LockCommandTreeNode extends CommandTreeNode {
   public LockCommandTreeNode(CommandTreeNode parent) {
      super(parent, "lock");
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
            PlayerData playerData = PlayerData.get(player);
            Waypoint waypoint = MMOCore.plugin.waypointManager.get(args[2]);
            if (!playerData.hasWaypoint(waypoint)) {
               sender.sendMessage(ChatColor.RED + "The waypoint " + args[2] + " is already locked.");
               return CommandResult.FAILURE;
            } else {
               PlayerData.get(player).lockWaypoint(waypoint);
               CommandVerbose.verbose(
                  sender,
                  CommandVerbose.CommandType.WAYPOINT,
                  ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " successfully locked " + ChatColor.GOLD + waypoint.getId() + ChatColor.YELLOW + "."
               );
               return CommandResult.SUCCESS;
            }
         }
      }
   }
}
