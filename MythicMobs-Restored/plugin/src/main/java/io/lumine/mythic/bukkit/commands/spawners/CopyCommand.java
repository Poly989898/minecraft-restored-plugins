package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CopyCommand extends Command<MythicBukkit> {
   public CopyCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 1) {
         CommandHelper.sendError(sender, "You must enter the name of a spawner to copy.");
         return true;
      } else {
         String name = args[0];
         if (MythicBukkit.inst().getSpawnerManager().getSpawnerByName(name) == null) {
            CommandHelper.sendError(sender, "You must enter a valid Mythic Spawner to copy!");
            return true;
         } else if (args.length < 2) {
            CommandHelper.sendError(sender, "You must enter a name for the new spawner.");
            return true;
         } else {
            String nameNew = args[1];
            MythicSpawner msNew = MythicBukkit.inst().getSpawnerManager().getSpawnerByName(args[1]);
            if (msNew != null) {
               CommandHelper.sendError(sender, "The name you entered for a new spawner is already in use!");
               return true;
            } else {
               Player p = (Player)sender;
               Location location = p.getTargetBlock((HashSet)null, 10).getLocation();
               if (location == null) {
                  CommandHelper.sendError(sender, "You must target a valid block to place the spawner!");
                  return true;
               } else {
                  if (MythicBukkit.inst().getSpawnerManager().copySpawner(name, nameNew, BukkitAdapter.adapt(location))) {
                     location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
                     location.getWorld().playEffect(location, Effect.EXTINGUISH, 0);
                     CommandHelper.sendSuccess(sender, "New spawner was cloned successfully!");
                  }

                  return true;
               }
            }
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(args[0], this.getPlugin().getSpawnerManager().getSpawners().stream().map(s -> s.getInternalName()).toList(), 2)
         : Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.copy";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "copy";
   }
}
