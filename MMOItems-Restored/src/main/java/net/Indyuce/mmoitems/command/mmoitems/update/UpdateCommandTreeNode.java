package net.Indyuce.mmoitems.command.mmoitems.update;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;

public class UpdateCommandTreeNode extends CommandTreeNode {
   public UpdateCommandTreeNode(CommandTreeNode var1) {
      super(var1, "update");
      this.addChild(new ListCommandTreeNode(this));
      this.addChild(new ApplyCommandTreeNode(this));
      this.addChild(new InfoCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      return CommandResult.THROW_USAGE;
   }
}
