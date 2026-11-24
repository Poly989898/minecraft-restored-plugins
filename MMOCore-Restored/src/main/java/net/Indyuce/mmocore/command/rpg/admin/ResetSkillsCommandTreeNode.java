package net.Indyuce.mmocore.command.rpg.admin;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import java.util.HashSet;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.command.api.CommandVerbose;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class ResetSkillsCommandTreeNode extends CommandTreeNode {
   public ResetSkillsCommandTreeNode(CommandTreeNode parent) {
      super(parent, "skills");
      this.addParameter(Parameter.PLAYER);
   }

   public CommandResult execute(CommandSender sender, String[] args) {
      if (args.length < 4) {
         return CommandResult.THROW_USAGE;
      } else {
         Player player = Bukkit.getPlayer(args[3]);
         if (player == null) {
            sender.sendMessage(ChatColor.RED + "Could not find the player called " + args[3] + ".");
            return CommandResult.FAILURE;
         } else {
            resetSkills(PlayerData.get(player));
            CommandVerbose.verbose(
               sender, CommandVerbose.CommandType.RESET, ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'s skill data was successfully reset."
            );
            return CommandResult.SUCCESS;
         }
      }
   }

   static void resetSkills(@NotNull PlayerData data) {
      data.mapSkillLevels().forEach((skill, ignored) -> data.resetSkillLevel(skill));

      while (data.hasSkillBound(0)) {
         data.unbindSkill(0);
      }

      data.setUnlockedItems(new HashSet<>());
   }
}
