package io.lumine.mythic.bukkit.commands.test;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.utils.MythicUtil;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class TauntCommand extends Command<MythicBukkit> {
   public TauntCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      LivingEntity target = MythicUtil.getTargetedEntity(player);
      if (MythicBukkit.inst().getAPIHelper().taunt(target, player)) {
         CommandHelper.sendSuccess(sender, "Taunted target mob.");
      } else {
         CommandHelper.sendError(sender, "Failed to taunt target mob.");
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.test.taunt";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "taunt";
   }
}
