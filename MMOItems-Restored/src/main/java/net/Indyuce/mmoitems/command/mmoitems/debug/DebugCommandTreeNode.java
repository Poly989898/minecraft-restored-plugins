package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;

public class DebugCommandTreeNode extends CommandTreeNode {
   public DebugCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "debug");
      this.addChild(new CheckStatCommandTreeNode(this));
      this.addChild(new CheckAttributeCommandTreeNode(this));
      this.addChild(new CheckTagCommandTreeNode(this));
      this.addChild(new SetTagCommandTreeNode(this));
      this.addChild(new CheckTagsCommandTreeNode(this));
      this.addChild(new InfoCommandTreeNode(this));
      this.addChild(new HealCommandTreeNode(this));
      this.addChild(new TestCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      return CommandResult.THROW_USAGE;
   }
}
