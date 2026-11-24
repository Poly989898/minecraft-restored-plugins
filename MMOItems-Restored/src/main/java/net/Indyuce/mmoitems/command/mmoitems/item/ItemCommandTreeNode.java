package net.Indyuce.mmoitems.command.mmoitems.item;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.command.CommandSender;

public class ItemCommandTreeNode extends CommandTreeNode {
   public ItemCommandTreeNode(CommandTreeNode var1) {
      super(var1, "item");
      this.addChild(new IdentifyCommandTreeNode(this));
      this.addChild(new UnidentifyCommandTreeNode(this));
      this.addChild(new RepairCommandTreeNode(this));
      this.addChild(new DeconstructCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      return CommandResult.THROW_USAGE;
   }
}
