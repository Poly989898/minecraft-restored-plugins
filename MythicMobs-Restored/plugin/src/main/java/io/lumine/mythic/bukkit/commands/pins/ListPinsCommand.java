package io.lumine.mythic.bukkit.commands.pins;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.skills.pins.MultiPin;
import io.lumine.mythic.core.skills.pins.Pin;
import io.lumine.mythic.core.skills.pins.SinglePin;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListPinsCommand extends Command<MythicBukkit> {
   public ListPinsCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      AbstractLocation playerLocation = BukkitAdapter.adapt(player.getLocation());
      CommandHelper.sendEditorMessage(sender, "<gold><bold>List of Pins:");
      Map<Pin, Double> pinDistanceMap = new HashMap<>();

      for (Entry<String, Pin> entry : this.getPlugin().getPinManager().getPins().entrySet()) {
         Pin pin = entry.getValue();
         if (pin instanceof SinglePin singlePin) {
            double distance = singlePin.getLocations().get(0).distance(playerLocation);
            pinDistanceMap.put(singlePin, distance);
         } else if (pin instanceof MultiPin multiPin) {
            double minDistance = Double.MAX_VALUE;

            for (AbstractLocation location : multiPin.getLocations()) {
               double distance = location.distance(playerLocation);
               minDistance = Math.min(minDistance, distance);
            }

            pinDistanceMap.put(multiPin, minDistance);
         }
      }

      for (Entry<Pin, Double> entryx : pinDistanceMap.entrySet().stream().sorted(Entry.comparingByValue()).collect(Collectors.toList())) {
         Pin pin = entryx.getKey();
         Double distance = entryx.getValue();
         StringBuilder message = new StringBuilder();
         if (pin instanceof SinglePin singlePin) {
            message.append("<red><click:run_command:/mm p removepin " + singlePin.getPack().getKey() + " " + singlePin.getPropertyNode() + ">\u2715</click> ");
            message.append(
               "<white>Pin <yellow><click:copy_to_clipboard:"
                  + singlePin.getPropertyNode()
                  + ">"
                  + singlePin.getPropertyNode()
                  + "</click> - <aqua>"
                  + singlePin.getLocations().get(0).toPoint().round(2).serializeShortForm()
                  + " (Distance: "
                  + distance
                  + ")"
            );
            Text.sendMessage(sender, message.toString());
         } else if (pin instanceof MultiPin multiPin) {
            message.append("<red><click:run_command:/mm p removepin " + multiPin.getPack().getKey() + " " + multiPin.getPropertyNode() + ">\u2715</click> ");
            message.append(
               "<white>Pin <yellow><click:copy_to_clipboard:"
                  + multiPin.getPropertyNode()
                  + ">"
                  + multiPin.getPropertyNode()
                  + "</click> (Distance: "
                  + distance
                  + ")"
            );
            Text.sendMessage(sender, message.toString());

            for (AbstractLocation location : multiPin.getLocations()) {
               StringBuilder message2 = new StringBuilder();
               message2.append(
                  "<red><click:run_command:/mm p removemulti "
                     + multiPin.getPack().getKey()
                     + " "
                     + multiPin.getPropertyNode()
                     + " "
                     + location.toPoint().round(2).serializeShortForm()
                     + ">\u2715</click> "
               );
               message2.append("<gray>- <aqua>" + location.toPoint().round(2).serializeShortForm());
               Text.sendMessage(sender, message2.toString());
            }
         }
      }

      CommandHelper.sendFooter(sender);
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.pins.list";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "list";
   }
}
