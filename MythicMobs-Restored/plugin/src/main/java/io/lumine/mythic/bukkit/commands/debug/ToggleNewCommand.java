package io.lumine.mythic.bukkit.commands.debug;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.logging.MythicDebug;
import java.util.List;
import org.bukkit.command.CommandSender;

public class ToggleNewCommand extends Command<MythicBukkit> {
   private static boolean clocksEnabled = true;

   public ToggleNewCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (MythicDebug.toggleDebugging()) {
         CommandHelper.sendSuccess(sender, "Debugger Disabled");
      } else {
         CommandHelper.sendSuccess(sender, "Debugger Enabled");
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.debug";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "new";
   }
}
