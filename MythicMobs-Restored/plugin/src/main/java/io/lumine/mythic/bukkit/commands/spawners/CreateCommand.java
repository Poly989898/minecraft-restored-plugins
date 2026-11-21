package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.numbers.RandomInt;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends Command<MythicBukkit> {
   public CreateCommand(Command<MythicBukkit> parent) {
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

      if (args.length < 2) {
         CommandHelper.sendError(sender, "Command Format: " + ChatColor.AQUA + "/mm spawners create [name] [mob_name]");
         return true;
      } else {
         String name = args[0];
         if (name == null) {
            CommandHelper.sendError(sender, "You must enter a name for the spawner.");
            return true;
         } else {
            Optional<MythicSpawner> maybeSpawner = MythicBukkit.inst().getSpawnerManager().getSpawnerByHashcode(name.hashCode());
            if (maybeSpawner.isPresent()) {
               CommandHelper.sendError(sender, "Spawner name must resolve to a unique hashcode.");
               return true;
            } else {
               RandomInt level = new RandomInt(1);
               String mobName;
               if (args[1].contains(":")) {
                  String[] split = args[1].split(":");
                  mobName = split[0];

                  try {
                     level = new RandomInt(split[1]);
                  } catch (Exception e) {
                     CommandHelper.sendError(sender, "Invalid mob level supplied: must be an integer or range (1, 2to5, etc).");
                     return true;
                  }
               } else {
                  mobName = args[1];
               }

               if (mobName == null) {
                  CommandHelper.sendError(sender, "You must enter a Mythic Mob for the spawner to spawn.");
                  return true;
               } else if (MythicBukkit.inst().getMobManager().getMythicMob(args[1]) == null) {
                  CommandHelper.sendError(sender, "You must enter a valid Mythic Mob. That one was not found!");
                  return true;
               } else {
                  Location location = null;
                  if (args.length > 2) {
                     try {
                        String[] part = args[2].split(",");
                        World w = Bukkit.getWorld(part[0]);
                        float x = Float.parseFloat(part[1]);
                        float y = Float.parseFloat(part[2]);
                        float z = Float.parseFloat(part[3]);
                        MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Parsing location using: " + w.toString() + "," + x + "," + y + "," + z);
                        if (w != null) {
                           location = new Location(w, x, y, z);
                        }
                     } catch (Exception ex) {
                        CommandHelper.sendError(sender, "Invalid location specified for spawner: location must be in format world,x,y,z");
                        return true;
                     }
                  } else {
                     if (!(sender instanceof Player player)) {
                        CommandHelper.sendError(sender, "Invalid location specified for spawner: location must be in format world,x,y,z");
                        return true;
                     }

                     if (optAtPlayer) {
                        location = player.getLocation();
                     } else {
                        location = player.getTargetBlock((HashSet)null, 10).getLocation();
                     }
                  }

                  if (location == null) {
                     CommandHelper.sendError(sender, "You must target a valid block to place the spawner!");
                     return true;
                  } else {
                     MythicSpawner ms = MythicBukkit.inst().getSpawnerManager().createSpawner(name, location, mobName);
                     if (ms != null) {
                        location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
                        location.getWorld().playEffect(location, Effect.EXTINGUISH, 0);
                        CommandHelper.sendSuccess(sender, "Spawner '" + name + "' was created successfully!");
                        ms.setMobLevel(level);
                     } else {
                        CommandHelper.sendError(sender, "Spawner creation failed: spawner name is probably already taken!");
                     }

                     return true;
                  }
               }
            }
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 2
         ? CollectionUtils.filterMatches(
            args[1], this.getPlugin().getMobManager().getMobTypes().stream().filter(mobType -> !mobType.isHidden()).map(MythicMob::getInternalName).toList(), 2
         )
         : null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.create";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "create";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"cr", "c"};
   }
}
