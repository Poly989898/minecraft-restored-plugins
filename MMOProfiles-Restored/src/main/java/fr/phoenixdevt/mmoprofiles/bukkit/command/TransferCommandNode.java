package fr.phoenixdevt.mmoprofiles.bukkit.command;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;

public class TransferCommandNode extends CommandTreeNode {
   public TransferCommandNode(CommandTreeNode parent) {
      super(parent, "convert-from-mmocore");
   }

   public CommandResult execute(CommandSender sender, String[] args) {
      return null;
   }
}
