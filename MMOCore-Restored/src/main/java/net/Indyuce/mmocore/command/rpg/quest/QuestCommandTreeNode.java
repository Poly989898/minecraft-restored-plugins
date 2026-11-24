package net.Indyuce.mmocore.command.rpg.quest;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;

public class QuestCommandTreeNode extends CommandTreeNode {
   public QuestCommandTreeNode(CommandTreeNode parent) {
      super(parent, "quest");
      this.addChild(new StartCommandTreeNode(this));
      this.addChild(new CancelCommandTreeNode(this));
      this.addChild(new FinishCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender sender, String[] args) {
      return CommandResult.THROW_USAGE;
   }
}
