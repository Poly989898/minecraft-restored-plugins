package net.Indyuce.mmocore.command.rpg.admin;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttributes;
import net.Indyuce.mmocore.command.api.CommandVerbose;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class ResetAttributesCommandTreeNode extends CommandTreeNode {
   public ResetAttributesCommandTreeNode(CommandTreeNode parent) {
      super(parent, "attributes");
      this.addParameter(Parameter.PLAYER);
      this.addParameter(new Parameter("(-reallocate)", (explore, list) -> list.add("-reallocate")));
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
            resetAttributes(PlayerData.get(player), givePoints);
            CommandVerbose.verbose(
               sender, CommandVerbose.CommandType.RESET, ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "'s attributes were successfully reset."
            );
            return CommandResult.SUCCESS;
         }
      }
   }

   static void resetAttributes(@NotNull PlayerData data, boolean givePoints) {
      if (givePoints) {
         int points = 0;

         for (PlayerAttributes.AttributeInstance ins : data.getAttributes().getInstances()) {
            points += ins.getBase();
            ins.setBase(0);
         }

         data.giveAttributePoints(points);
      } else {
         for (PlayerAttribute attribute : MMOCore.plugin.attributeManager.getAll()) {
            attribute.resetAdvancement(data, true);
            data.getAttributes().getInstance(attribute).setBase(0);
         }
      }
   }
}
