package net.Indyuce.mmocore.command.rpg.waypoint;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;

public class WaypointsCommandTreeNode extends CommandTreeNode {
   public WaypointsCommandTreeNode(CommandTreeNode parent) {
      super(parent, "waypoints");
      this.addChild(new UnlockCommandTreeNode(this));
      this.addChild(new OpenCommandTreeNode(this));
      this.addChild(new TeleportCommandTreeNode(this));
      this.addChild(new ItemCommandTreeNode(this));
      this.addChild(new LockCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender sender, String[] args) {
      return CommandResult.THROW_USAGE;
   }
}
