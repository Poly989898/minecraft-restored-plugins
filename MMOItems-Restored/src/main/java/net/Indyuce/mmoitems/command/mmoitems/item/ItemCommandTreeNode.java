package net.Indyuce.mmoitems.command.mmoitems.item;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;

public class ItemCommandTreeNode extends CommandTreeNode {
   public ItemCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "item");
      this.addChild(new IdentifyCommandTreeNode(this));
      this.addChild(new UnidentifyCommandTreeNode(this));
      this.addChild(new RepairCommandTreeNode(this));
      this.addChild(new DeconstructCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      return CommandResult.THROW_USAGE;
   }
}
