package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;

public class DebugCommandTreeNode extends CommandTreeNode {
   public DebugCommandTreeNode(CommandTreeNode var1) {
      super(var1, "debug");
      this.addChild(new CheckStatCommandTreeNode(this));
      this.addChild(new CheckAttributeCommandTreeNode(this));
      this.addChild(new CheckTagCommandTreeNode(this));
      this.addChild(new SetTagCommandTreeNode(this));
      this.addChild(new CheckTagsCommandTreeNode(this));
      this.addChild(new InfoCommandTreeNode(this));
      this.addChild(new HealCommandTreeNode(this));
      this.addChild(new TestCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      return CommandResult.THROW_USAGE;
   }
}
