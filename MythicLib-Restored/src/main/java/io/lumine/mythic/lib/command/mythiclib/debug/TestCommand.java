package io.lumine.mythic.lib.command.mythiclib.debug;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import org.bukkit.command.CommandSender;

public class TestCommand extends CommandTreeNode {
   public TestCommand(CommandTreeNode parent) {
      super(parent, "test");
   }

   @Override
   public CommandTreeNode.CommandResult execute(CommandSender sender, String[] args) {
      return CommandTreeNode.CommandResult.SUCCESS;
   }
}
