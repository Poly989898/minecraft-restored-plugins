package net.Indyuce.mmoitems.command.mmoitems.update;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;

public class UpdateCommandTreeNode extends CommandTreeNode {
   public UpdateCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "update");
      this.addChild(new ListCommandTreeNode(this));
      this.addChild(new ApplyCommandTreeNode(this));
      this.addChild(new InfoCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      return CommandResult.THROW_USAGE;
   }
}
