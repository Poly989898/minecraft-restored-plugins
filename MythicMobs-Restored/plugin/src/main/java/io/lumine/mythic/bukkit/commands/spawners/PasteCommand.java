package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.spawning.spawners.SpawnerSlice;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PasteCommand extends Command<MythicBukkit> {
   public PasteCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (SpawnerSlice.PasteSpawners(BukkitAdapter.adapt(((Player)sender).getLocation()))) {
         CommandHelper.sendSuccess(sender, "Spawners pasted from the clipboard!");
      } else {
         CommandHelper.sendError(sender, "There are no spawners to paste in the clipboard (or an error occured!)");
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.paste";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "paste";
   }
}
