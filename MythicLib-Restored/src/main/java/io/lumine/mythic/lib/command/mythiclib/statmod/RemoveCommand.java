package io.lumine.mythic.lib.command.mythiclib.statmod;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RemoveCommand extends CommandTreeNode {
   public RemoveCommand(@NotNull CommandTreeNode parent) {
      super(parent, "remove");
      this.addParameter(Parameter.PLAYER);
      this.addParameter(Parameter.STAT);
      this.addParameter(AddCommand.STAT_KEY);
   }

   @Override
   public CommandTreeNode.CommandResult execute(CommandSender sender, String[] args) {
      if (args.length < 4) {
         return CommandTreeNode.CommandResult.THROW_USAGE;
      } else {
         Player target = Bukkit.getPlayer(args[2]);
         if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return CommandTreeNode.CommandResult.FAILURE;
         } else {
            String statName = UtilityMethods.enumName(args[3]);
            MMOPlayerData playerData = MMOPlayerData.get(target);
            String key = args.length > 4 ? args[4] : "default";
            playerData.getStatMap().getInstance(statName).removeIf(key::equals);
            return CommandTreeNode.CommandResult.SUCCESS;
         }
      }
   }
}
