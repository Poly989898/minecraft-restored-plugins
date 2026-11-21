package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.api.adapters.AbstractStructure;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.command.CommandSender;

public class ListValidStructuresCommand extends Command<MythicBukkit> {
   public ListValidStructuresCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      CommandHelper.send(sender, "Structures Loaded:");

      for (AbstractStructure struct : this.getPlugin().getVolatileCodeHandler().getWorldHandler().getStructures()) {
         String value = struct.getKey();
         CommandHelper.send(sender, "- " + value);
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.listvalidstructures";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "listvalidstructures";
   }
}
