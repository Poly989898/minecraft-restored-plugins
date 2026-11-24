package fr.phoenixdevt.mmoprofiles.bukkit.command;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommandNode extends CommandTreeNode {
   public TestCommandNode(CommandTreeNode parent) {
      super(parent, "test");
   }

   public CommandResult execute(CommandSender commandSender, String[] strings) {
      Player player = (Player)commandSender;
      return CommandResult.SUCCESS;
   }
}
