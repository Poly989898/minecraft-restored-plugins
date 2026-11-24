package net.Indyuce.mmocore.command.rpg.admin;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.command.api.CommandVerbose;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommandTreeNode extends CommandTreeNode {
   public ResetCommandTreeNode(CommandTreeNode parent) {
      super(parent, "reset");
      this.addChild(new ResetClassesCommandTreeNode(this));
      this.addChild(new ResetLevelsCommandTreeNode(this));
      this.addChild(new ResetSkillsCommandTreeNode(this));
      this.addChild(new ResetQuestsCommandTreeNode(this));
      this.addChild(new ResetAttributesCommandTreeNode(this));
      this.addChild(new ResetWaypointsCommandTreeNode(this));
      this.addChild(new ResetSkillTreesCommandTreeNode(this));
      this.addChild(new ResetCommandTreeNode.ResetAllCommandTreeNode(this));
   }

   public CommandResult execute(CommandSender sender, String[] args) {
      return CommandResult.THROW_USAGE;
   }

   public static class ResetAllCommandTreeNode extends CommandTreeNode {
      public ResetAllCommandTreeNode(CommandTreeNode parent) {
         super(parent, "all");
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
               boolean givePoints = args.length > 4 && args[4].equalsIgnoreCase("-reallocate");
               PlayerData data = PlayerData.get(player);
               ResetClassesCommandTreeNode.resetClasses(data);
               ResetLevelsCommandTreeNode.resetLevels(data);
               ResetSkillsCommandTreeNode.resetSkills(data);
               ResetQuestsCommandTreeNode.resetQuests(data);
               ResetAttributesCommandTreeNode.resetAttributes(data, givePoints);
               ResetWaypointsCommandTreeNode.resetWaypoints(data);
               ResetSkillTreesCommandTreeNode.resetSkillTrees(data);
               data.getItemClaims().clear();
               CommandVerbose.verbose(
                  sender, CommandVerbose.CommandType.RESET, ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'s data was successfully reset."
               );
               return CommandResult.SUCCESS;
            }
         }
      }
   }
}
