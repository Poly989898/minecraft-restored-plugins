package io.lumine.mythic.bukkit.commands.test;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.utils.MythicUtil;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ReduceThreatCommand extends Command<MythicBukkit> {
   public ReduceThreatCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      LivingEntity target = MythicUtil.getTargetedEntity(player);
      double amount = 100.0;
      if (args.length > 0) {
         try {
            amount = Double.valueOf(args[0]);
         } catch (Exception e) {
            CommandHelper.sendError(sender, "Amount must be a number.");
         }
      }

      if (MythicBukkit.inst().getAPIHelper().reduceThreat(target, player, amount)) {
         CommandHelper.sendSuccess(sender, "Reduced mob threat by &a" + amount + "&b.");
      } else {
         CommandHelper.sendError(sender, "Failed to reduuce threat on target mob.");
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.test.reducethreat";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "reducethreat";
   }
}
