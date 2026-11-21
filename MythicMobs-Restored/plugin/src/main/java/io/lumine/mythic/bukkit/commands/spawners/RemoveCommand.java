package io.lumine.mythic.bukkit.commands.spawners;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Collections;
import java.util.List;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class RemoveCommand extends Command<MythicBukkit> {
   public RemoveCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 1) {
         CommandHelper.sendError(sender, "You must enter the name of a mob spawner to remove!");
         return true;
      } else {
         String name = args[0];
         if (name.startsWith("g:")) {
            String group = name.substring(2);

            for (MythicSpawner ms : this.getPlugin().getSpawnerManager().getSpawnersByGroup(group)) {
               if (this.getPlugin().getSpawnerManager().removeSpawner(ms)) {
                  Location location = BukkitAdapter.adapt(ms.getLocation());
                  World world = location.getWorld();
                  world.playEffect(location, Effect.SMOKE, 0);
                  world.playEffect(location, Effect.EXTINGUISH, 0);
               }
            }

            CommandHelper.sendSuccess(sender, "All spawners in group " + group + " have been removed.");
         } else if (!name.contains("*") && !name.contains("?")) {
            MythicSpawner msx = this.getPlugin().getSpawnerManager().getSpawnerByName(name);
            if (msx == null) {
               CommandHelper.sendError(sender, "The spawner entered does not exist!");
               return true;
            }

            if (this.getPlugin().getSpawnerManager().removeSpawner(msx)) {
               Location location = BukkitAdapter.adapt(msx.getLocation());
               World world = location.getWorld();
               world.playEffect(location, Effect.SMOKE, 0);
               world.playEffect(location, Effect.EXTINGUISH, 0);
               CommandHelper.sendSuccess(sender, "Spawner was removed successfully!");
            }
         } else {
            for (MythicSpawner msxx : Lists.newArrayList(this.getPlugin().getSpawnerManager().getSpawners())) {
               if (msxx.getName().matches(name.replace("?", ".?").replace("*", ".*?")) && this.getPlugin().getSpawnerManager().removeSpawner(msxx)) {
                  Location location = BukkitAdapter.adapt(msxx.getLocation());
                  World world = location.getWorld();
                  world.playEffect(location, Effect.SMOKE, 0);
                  world.playEffect(location, Effect.EXTINGUISH, 0);
               }
            }

            CommandHelper.sendSuccess(sender, "Spawners matching pattern " + name + " have been removed.");
         }

         return true;
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
      return "mythicmobs.command.spawners.remove";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "remove";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"re", "delete", "r"};
   }
}
