package io.lumine.mythic.bukkit.commands.test;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.utils.MythicUtil;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class AddThreatCommand extends Command<MythicBukkit> {
   public AddThreatCommand(Command<MythicBukkit> parent) {
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

      if (MythicBukkit.inst().getAPIHelper().addThreat(target, player, amount)) {
         CommandHelper.sendSuccess(sender, "Added &a" + amount + " &bthreat to target mob.");
      } else {
         CommandHelper.sendError(sender, "Failed to add threat to target mob.");
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.test.addthreat";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "addthreat";
   }
}
