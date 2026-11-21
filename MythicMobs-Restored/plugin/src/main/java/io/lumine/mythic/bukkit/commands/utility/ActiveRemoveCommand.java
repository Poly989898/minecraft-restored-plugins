package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class ActiveRemoveCommand extends Command<MythicBukkit> {
   public ActiveRemoveCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Entity l = Bukkit.getEntity(UUID.fromString(args[0]));
      if (l == null) {
         CommandHelper.sendError(sender, "You must target a valid entity!");
         return true;
      } else {
         l.remove();
         CommandHelper.sendSuccess(sender, "Removed entity");
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.activeremove";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "activeremove";
   }
}
