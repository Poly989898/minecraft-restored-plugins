package net.Indyuce.mmoitems.command.mmoitems.list;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AbilityCommandTreeNode extends CommandTreeNode {
   public AbilityCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "ability");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      sender.sendMessage(
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
      sender.sendMessage(ChatColor.WHITE + "Here are all the abilities you can bind to items.");
      sender.sendMessage(
         ChatColor.WHITE
            + "The values inside brackets are "
            + ChatColor.UNDERLINE
            + "modifiers"
            + ChatColor.WHITE
            + " which allow you to change the ability values (cooldown, damage...)"
      );

      for (RegisteredSkill registeredSkill : MMOItems.plugin.getSkills().getAll()) {
         String text = ChatColor.GRAY + String.join(ChatColor.WHITE + ", " + ChatColor.GRAY, registeredSkill.getHandler().getModifiers());
         text = ChatColor.WHITE + "(" + text + ChatColor.WHITE + ")";
         sender.sendMessage("* " + ChatColor.LIGHT_PURPLE + registeredSkill.getName() + " " + text);
      }

      return CommandResult.SUCCESS;
   }
}
