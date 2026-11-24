package net.Indyuce.mmoitems.command.mmoitems.list;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.stat.LuteAttackEffectStat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class LuteAttackCommandTreeNode extends CommandTreeNode {
   public LuteAttackCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "lute");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      sender.sendMessage(
         ChatColor.DARK_GRAY
            + ""
            + ChatColor.STRIKETHROUGH
            + "-----------------["
            + ChatColor.LIGHT_PURPLE
            + " Lute Attack Effects "
            + ChatColor.DARK_GRAY
            + ""
            + ChatColor.STRIKETHROUGH
            + "]-----------------"
      );

      for (LuteAttackEffectStat.LuteAttackEffect luteAttackEffect : LuteAttackEffectStat.LuteAttackEffect.values()) {
         sender.sendMessage("* " + ChatColor.LIGHT_PURPLE + luteAttackEffect.name());
      }

      return CommandResult.SUCCESS;
   }
}
