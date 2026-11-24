package net.Indyuce.mmocore.command.rpg.admin;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.command.api.CommandVerbose;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class ResetClassesCommandTreeNode extends CommandTreeNode {
   public ResetClassesCommandTreeNode(CommandTreeNode parent) {
      super(parent, "classes");
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
            resetClasses(PlayerData.get(player));
            CommandVerbose.verbose(
               sender, CommandVerbose.CommandType.RESET, ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'s classes were successfully reset."
            );
            return CommandResult.SUCCESS;
         }
      }
   }

   static void resetClasses(@NotNull PlayerData data) {
      MMOCore.plugin.classManager.getAll().forEach(data::unloadClassInfo);
      MMOCore.plugin.playerDataManager.getDefaultData().apply(data);
      data.setClass(MMOCore.plugin.classManager.getDefaultClass());
   }
}
