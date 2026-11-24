package net.Indyuce.mmoitems.command.mmoitems.stations;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;

public class StationsCommandTreeNode extends CommandTreeNode {
   public StationsCommandTreeNode(CommandTreeNode var1) {
      super(var1, "stations");
      this.addChild(new OpenCommandTreeNode(this));
      this.addChild(new ListCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      return CommandResult.THROW_USAGE;
   }
}
