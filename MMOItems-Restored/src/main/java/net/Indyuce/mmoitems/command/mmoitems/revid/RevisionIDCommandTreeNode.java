package net.Indyuce.mmoitems.command.mmoitems.revid;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.command.CommandSender;

public class RevisionIDCommandTreeNode extends CommandTreeNode {
   public static final Parameter TYPE_OR_ALL = new Parameter("<type>", (var0, var1) -> {
      MMOItems.plugin.getTypes().getAll().forEach(var1x -> var1.add(var1x.getId()));
      var1.add("ALL");
   });

   public RevisionIDCommandTreeNode(CommandTreeNode var1) {
      super(var1, "revid");
      this.addChild(new RevIDActionCommandTreeNode(this, "increase", var0 -> Math.min(var0 + 1, Integer.MAX_VALUE)));
      this.addChild(new RevIDActionCommandTreeNode(this, "decrease", var0 -> Math.max(var0 - 1, 1)));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      return CommandResult.THROW_USAGE;
   }
}
