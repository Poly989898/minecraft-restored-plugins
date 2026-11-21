package io.lumine.mythic.bukkit.commands.debug;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.command.CommandSender;

public class ToggleClockCommand extends Command<MythicBukkit> {
   private static boolean clocksEnabled = true;

   public ToggleClockCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (clocksEnabled) {
         this.getPlugin().getClock().unload();
         CommandHelper.sendSuccess(sender, "Clocks Disabled");
         clocksEnabled = false;
      } else {
         this.getPlugin().getClock().load(this.getPlugin());
         CommandHelper.sendSuccess(sender, "Clocks Enabled");
         clocksEnabled = true;
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
      return "toggleclock";
   }
}
