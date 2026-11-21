package io.lumine.mythic.bukkit.commands.pins;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.skills.pins.Pin;
import io.lumine.mythic.core.skills.pins.SinglePin;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MovePinCommand extends Command<MythicBukkit> {
   public MovePinCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      String nodeName = args[0];
      AbstractLocation location = BukkitAdapter.adapt(player.getLocation());
      Optional<Pin> maybePin = this.getPlugin().getPinManager().getPin(nodeName);
      if (maybePin.isEmpty()) {
         CommandHelper.sendError(sender, "Pin not found.");
         return true;
      } else {
         Pin pin = maybePin.get();
         if (pin instanceof SinglePin singlePin) {
            this.getPlugin().getPinManager().movePin(singlePin, location);
            CommandHelper.sendSuccess(
               sender,
               "Updated Pin <gold><click:copy_to_clipboard:"
                  + nodeName
                  + ">"
                  + nodeName
                  + " <gray>[<yellow><click:copy_to_clipboard:@Pin{pin="
                  + nodeName
                  + "}>Targeter<gray>]"
            );
         } else {
            CommandHelper.sendError(sender, "MultiPin does not support moving.");
         }

         return true;
      }
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
      return "move";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"m"};
   }
}
