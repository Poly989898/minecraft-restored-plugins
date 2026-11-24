package net.Indyuce.mmoitems.command.mmoitems.list;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AbilityCommandTreeNode extends CommandTreeNode {
   public AbilityCommandTreeNode(CommandTreeNode var1) {
      super(var1, "ability");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      var1.sendMessage(
         ChatColor.DARK_GRAY
            + ""
            + ChatColor.STRIKETHROUGH
            + "-----------------["
            + ChatColor.LIGHT_PURPLE
            + " Abilities "
            + ChatColor.DARK_GRAY
            + ""
            + ChatColor.STRIKETHROUGH
            + "]-----------------"
      );
      var1.sendMessage(ChatColor.WHITE + "Here are all the abilities you can bind to items.");
      var1.sendMessage(
         ChatColor.WHITE
            + "The values inside brackets are "
            + ChatColor.UNDERLINE
            + "modifiers"
            + ChatColor.WHITE
            + " which allow you to change the ability values (cooldown, damage...)"
      );

      for (RegisteredSkill var4 : MMOItems.plugin.getSkills().getAll()) {
         String var5 = ChatColor.GRAY + String.join(ChatColor.WHITE + ", " + ChatColor.GRAY, var4.getHandler().getModifiers());
         var5 = ChatColor.WHITE + "(" + var5 + ChatColor.WHITE + ")";
         var1.sendMessage("* " + ChatColor.LIGHT_PURPLE + var4.getName() + " " + var5);
      }

      return CommandResult.SUCCESS;
   }
}
