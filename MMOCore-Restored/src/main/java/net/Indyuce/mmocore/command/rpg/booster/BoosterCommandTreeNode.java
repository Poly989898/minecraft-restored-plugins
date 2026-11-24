package net.Indyuce.mmocore.command.rpg.booster;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;

public class BoosterCommandTreeNode extends CommandTreeNode {
   public BoosterCommandTreeNode(CommandTreeNode parent) {
      super(parent, "booster");
      this.addChild(new CreateCommandTreeNode(this));
      this.addChild(new ListCommandTreeNode(this));
      this.addChild(new RemoveCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender sender, String[] args) {
      return CommandResult.THROW_USAGE;
   }
}
