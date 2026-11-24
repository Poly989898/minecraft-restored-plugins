package io.lumine.mythic.lib.command.mythiclib.debug;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealthScaleCommand extends CommandTreeNode {
   public HealthScaleCommand(CommandTreeNode parent) {
      super(parent, "healthscale");
      this.addParameter(Parameter.AMOUNT);
      this.addParameter(Parameter.PLAYER.optional(true));
   }

   @Override
   public CommandTreeNode.CommandResult execute(CommandSender sender, String[] args) {
      if (args.length < 3) {
         return CommandTreeNode.CommandResult.THROW_USAGE;
      }

      if (!(sender instanceof Player) && args.length < 4) {
         MythicLib.plugin.getLogger().info("Please specify a player.");
         return CommandTreeNode.CommandResult.FAILURE;
      }

      double scale;
      try {
         scale = Double.parseDouble(args[2]);
      } catch (Exception exception) {
         MythicLib.plugin.getLogger().info("Could not read scale amount");
         return CommandTreeNode.CommandResult.FAILURE;
      }

      Player target = args.length > 3 ? Bukkit.getPlayer(args[3]) : (Player)sender;
      if (target == null) {
         MythicLib.plugin.getLogger().info("Could not find player " + args[3] + ".");
         return CommandTreeNode.CommandResult.FAILURE;
      } else {
         target.setHealthScaled(true);
         target.setHealthScale(scale);
         MythicLib.plugin.getLogger().info(args[0] + " Health has been scaled!");
         return CommandTreeNode.CommandResult.SUCCESS;
      }
   }
}
