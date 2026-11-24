package net.Indyuce.mmoitems.command.mmoitems.revid;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.command.CommandSender;

public class RevisionIDCommandTreeNode extends CommandTreeNode {
   public static final Parameter TYPE_OR_ALL = new Parameter("<type>", (commandTreeExplorer, texts) -> {
      MMOItems.plugin.getTypes().getAll().forEach(type -> texts.add(type.getId()));
      texts.add("ALL");
   });

   public RevisionIDCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "revid");
      this.addChild(new RevIDActionCommandTreeNode(this, "increase", value -> Math.min(value + 1, Integer.MAX_VALUE)));
      this.addChild(new RevIDActionCommandTreeNode(this, "decrease", value -> Math.max(value - 1, 1)));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      return CommandResult.THROW_USAGE;
   }
}
