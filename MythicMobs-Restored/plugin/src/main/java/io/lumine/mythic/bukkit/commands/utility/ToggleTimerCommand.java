package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.command.CommandSender;

public class ToggleTimerCommand extends Command<MythicBukkit> {
   public ToggleTimerCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      boolean bool = this.getPlugin().getClock().isReloadingPause();
      if (bool) {
         this.getPlugin().getClock().setReloadingPause(false);
         CommandHelper.sendSuccess(sender, "Toggled timer on");
      } else {
         this.getPlugin().getClock().setReloadingPause(true);
         CommandHelper.sendSuccess(sender, "Toggled timer off");
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.toggletimer";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "toggletimer";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"tt"};
   }
}
