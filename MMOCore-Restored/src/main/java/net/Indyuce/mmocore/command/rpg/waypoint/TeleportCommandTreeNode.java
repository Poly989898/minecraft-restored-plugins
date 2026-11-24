package net.Indyuce.mmocore.command.rpg.waypoint;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.waypoint.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommandTreeNode extends CommandTreeNode {
   public TeleportCommandTreeNode(CommandTreeNode parent) {
      super(parent, "teleport");
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
            player.teleport(waypoint.getLocation());
            sender.sendMessage(
               ChatColor.YELLOW
                  + "Successfully teleported "
                  + ChatColor.GOLD
                  + player.getName()
                  + ChatColor.YELLOW
                  + " to "
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
