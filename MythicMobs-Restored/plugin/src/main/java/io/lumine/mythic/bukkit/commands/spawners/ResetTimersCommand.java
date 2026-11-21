package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ResetTimersCommand extends Command<MythicBukkit> {
   public ResetTimersCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length == 0) {
         for (MythicSpawner ms : MythicBukkit.inst().getSpawnerManager().getSpawners()) {
            ms.resetTimers();
         }

         sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.GREEN + "Spawner timers reset for all spawners");
         return true;
      } else {
         String name = args[0];
         if (name.startsWith("g:")) {
            String group = name.substring(2);

            for (MythicSpawner ms : MythicBukkit.inst().getSpawnerManager().getSpawnersByGroup(group)) {
               ms.resetTimers();
            }

            sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.GREEN + "Spawner timers reset on group " + group);
         } else if (name.equals("*")) {
            for (MythicSpawner ms : MythicBukkit.inst().getSpawnerManager().getSpawners()) {
               ms.resetTimers();
            }

            sender.sendMessage(ChatColor.GOLD + "[MythicMobs] Spawner timers reset on all spawners!");
         } else if (!name.contains("*") && !name.contains("?")) {
            MythicSpawner ms = MythicBukkit.inst().getSpawnerManager().getSpawnerByName(name);
            if (ms == null) {
               sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "You must enter a valid Mythic Spawner. That one was not found!");
               return true;
            }

            ms.resetTimers();
            sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.GREEN + "Spawner timers reset on spawner " + name);
         } else {
            for (MythicSpawner ms : MythicBukkit.inst().getSpawnerManager().getSpawners()) {
               if (ms.getName().matches(name.replace("?", ".?").replace("*", ".*?"))) {
                  ms.resetTimers();
               }
            }

            sender.sendMessage(ChatColor.GOLD + "[MythicMobs] Spawner timers reset on spawners matching pattern " + name + "!");
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
      return "mythicmobs.command.spawners.resettimers";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "resettimers";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"rt"};
   }
}
