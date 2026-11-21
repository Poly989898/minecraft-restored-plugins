package io.lumine.mythic.bukkit.commands.pins;

import io.lumine.mythic.api.adapters.AbstractLocation;
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

public class SwapPinCommand extends Command<MythicBukkit> {
   public SwapPinCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      String node1Name = args[0];
      String node2Name = args[1];
      Optional<Pin> maybePin1 = this.getPlugin().getPinManager().getPin(node1Name);
      if (maybePin1.isEmpty()) {
         CommandHelper.sendError(sender, "Pin 1 not found.");
         return true;
      } else {
         Pin pin1 = maybePin1.get();
         Optional<Pin> maybePin2 = this.getPlugin().getPinManager().getPin(node2Name);
         if (maybePin2.isEmpty()) {
            CommandHelper.sendError(sender, "Pin 2 not found.");
            return true;
         } else {
            Pin pin2 = maybePin2.get();
            if (!(pin1 instanceof SinglePin)) {
               CommandHelper.sendError(sender, "MultiPin does not support moving.");
            }

            if (!(pin2 instanceof SinglePin)) {
               CommandHelper.sendError(sender, "MultiPin does not support moving.");
            }

            SinglePin single1Pin = (SinglePin)pin1;
            SinglePin single2Pin = (SinglePin)pin2;
            AbstractLocation pin1Location = single1Pin.getLocations().get(0);
            AbstractLocation pin2Location = single2Pin.getLocations().get(0);
            this.getPlugin().getPinManager().movePin(single1Pin, pin2Location);
            this.getPlugin().getPinManager().movePin(single2Pin, pin1Location);
            CommandHelper.sendSuccess(sender, "Swapped locations of " + node1Name + " and " + node2Name);
            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         return CollectionUtils.filterMatches(args[0], this.getPlugin().getPinManager().getPins().keySet().stream().toList(), 2);
      } else {
         return args.length == 2
            ? CollectionUtils.filterMatches(args[1], this.getPlugin().getPinManager().getPins().keySet().stream().toList(), 2)
            : Collections.emptyList();
      }
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.pins";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "swap";
   }
}
