package net.Indyuce.mmoitems.command.mmoitems.list;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.stat.LuteAttackEffectStat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class LuteAttackCommandTreeNode extends CommandTreeNode {
   public LuteAttackCommandTreeNode(CommandTreeNode var1) {
      super(var1, "lute");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      var1.sendMessage(
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

      for (LuteAttackEffectStat.LuteAttackEffect var6 : LuteAttackEffectStat.LuteAttackEffect.values()) {
         var1.sendMessage("* " + ChatColor.LIGHT_PURPLE + var6.name());
      }

      return CommandResult.SUCCESS;
   }
}
