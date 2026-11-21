package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.spawning.spawners.SpawnerSlice;
import java.util.List;
import org.bukkit.command.CommandSender;

public class UndoCommand extends Command<MythicBukkit> {
   public UndoCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (SpawnerSlice.Undo()) {
         CommandHelper.sendSuccess(sender, "Last spawner clipboard changes undone!");
      } else {
         CommandHelper.sendError(sender, "There are no spawners in the clipboard to undo an action on!");
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.undo";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "undo";
   }
}
