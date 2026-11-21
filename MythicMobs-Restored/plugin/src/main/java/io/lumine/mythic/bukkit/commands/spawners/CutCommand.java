package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.spawning.spawners.SpawnerSlice;
import java.util.Collection;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CutCommand extends Command<MythicBukkit> {
   public CutCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 1) {
         CommandHelper.sendError(sender, "You must enter a pattern of spawners to cut.");
         return true;
      } else {
         String name = args[0];
         Collection<MythicSpawner> msl = MythicBukkit.inst().getSpawnerManager().getSpawnersByString(BukkitAdapter.adapt((Player)sender).getLocation(), name);
         if (msl.size() == 0) {
            CommandHelper.sendError(sender, "Your entry returned no spawners to cut.");
            return true;
         } else {
            CommandHelper.sendSuccess(sender, "Cut <aqua>" + msl.size() + "<green> spawners to the clipboard!");
            SpawnerSlice.CutSpawners(msl, BukkitAdapter.adapt(((Player)sender).getLocation()));
            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.cut";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "cut";
   }
}
