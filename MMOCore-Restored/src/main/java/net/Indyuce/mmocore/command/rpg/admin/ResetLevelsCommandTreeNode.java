package net.Indyuce.mmocore.command.rpg.admin;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.command.api.CommandVerbose;
import net.Indyuce.mmocore.experience.Profession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class ResetLevelsCommandTreeNode extends CommandTreeNode {
   public ResetLevelsCommandTreeNode(CommandTreeNode parent) {
      super(parent, "levels");
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
            resetLevels(PlayerData.get(player));
            CommandVerbose.verbose(
               sender, CommandVerbose.CommandType.RESET, ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'s levels were successfully reset."
            );
            return CommandResult.SUCCESS;
         }
      }
   }

   static void resetLevels(@NotNull PlayerData data) {
      data.setLevel(MMOCore.plugin.playerDataManager.getDefaultData().getLevel());
      data.setExperience(0.0);
      data.getProfess().resetAdvancement(data, true);

      for (Profession profession : MMOCore.plugin.professionManager.getAll()) {
         data.getCollectionSkills().setExperience(profession, 0.0);
         data.getCollectionSkills().setLevel(profession, 0);
         profession.resetAdvancement(data, true);
      }
   }
}
