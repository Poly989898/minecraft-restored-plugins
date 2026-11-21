package io.lumine.mythic.bukkit.commands.pins;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleViewingPinsCommand extends Command<MythicBukkit> {
   public ToggleViewingPinsCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      if (this.getPlugin().getPinManager().toggleViewingPins(BukkitAdapter.adapt(player))) {
         CommandHelper.sendSuccess(sender, "Pins now visible");
      } else {
         CommandHelper.sendSuccess(sender, "Pins no longer visible");
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(args[0], this.getPlugin().getPinManager().getPins().keySet().stream().toList(), 2)
         : Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.pins";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "toggleviewing";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"tv"};
   }
}
