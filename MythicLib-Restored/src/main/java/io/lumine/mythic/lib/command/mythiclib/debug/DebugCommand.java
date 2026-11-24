package io.lumine.mythic.lib.command.mythiclib.debug;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import org.bukkit.command.CommandSender;

public class DebugCommand extends CommandTreeNode {
   public DebugCommand(CommandTreeNode parent) {
      super(parent, "debug");
      this.addChild(new LogsCommand(this));
      this.addChild(new StatsCommand(this));
      this.addChild(new VersionsCommand(this));
      this.addChild(new AttributesCommand(this));
      this.addChild(new HealthScaleCommand(this));
      this.addChild(new TestCommand(this));
      this.addChild(new ParseCommand(this));
   }

   @Override
   public CommandTreeNode.CommandResult execute(CommandSender sender, String[] args) {
      return CommandTreeNode.CommandResult.THROW_USAGE;
   }
}
