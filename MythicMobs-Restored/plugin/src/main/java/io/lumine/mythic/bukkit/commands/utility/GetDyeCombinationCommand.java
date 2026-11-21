package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import java.util.List;
import org.bukkit.command.CommandSender;

public class GetDyeCombinationCommand extends Command<MythicBukkit> {
   public GetDyeCombinationCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Chroma in = Chroma.of(args[0]);
      if (args.length > 1) {
         int dyes = Integer.parseInt(args[1]);
      } else {
         int dyes = 3;
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.getdyecombination";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "getdyecombination";
   }
}
