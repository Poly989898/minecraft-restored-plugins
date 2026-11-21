package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoveCommand extends Command<MythicBukkit> {
   public MoveCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      boolean optAtPlayer = false;
      if (args != null && args.length > 1 && args[0].startsWith("-")) {
         if (args[0].contains("p")) {
            optAtPlayer = true;
         }

         args = Arrays.copyOfRange(args, 1, args.length);
      }

      if (args.length < 1) {
         CommandHelper.sendError(sender, "You must enter the name of a spawner to move.");
         return true;
      } else {
         String name = args[0];
         if (MythicBukkit.inst().getSpawnerManager().getSpawnerByName(name) == null) {
            CommandHelper.sendError(sender, "You must enter a valid Mythic Spawner to move!");
            return true;
         } else {
            Player p = (Player)sender;
            Location location;
            if (optAtPlayer) {
               location = p.getLocation();
            } else {
               location = p.getTargetBlock((HashSet)null, 10).getLocation();
               if (location == null) {
                  CommandHelper.sendError(sender, "You must target a valid block to move the spawner!");
                  return true;
               }
            }

            if (MythicBukkit.inst().getSpawnerManager().moveSpawner(name, BukkitAdapter.adapt(location))) {
               location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
               location.getWorld().playEffect(location, Effect.EXTINGUISH, 0);
               CommandHelper.sendSuccess(sender, "Spawner " + name + " was moved successfully!");
            }

            return true;
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
      return "mythicmobs.command.spawners.move";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "move";
   }
}
