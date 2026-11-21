package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ActivateCommand extends Command<MythicBukkit> {
   public ActivateCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 1) {
         sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "You must enter the name of a mob spawner to activate!");
      }

      String name = args[0];
      if (name.startsWith("g:")) {
         String group = name.substring(2);

         for (MythicSpawner ms : MythicBukkit.inst().getSpawnerManager().getSpawnersByGroup(group)) {
            ms.ActivateSpawner();
         }

         sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.GREEN + "All spawners in group " + group + " have been activated!");
      } else if (name.equals("*")) {
         for (MythicSpawner ms : MythicBukkit.inst().getSpawnerManager().getSpawners()) {
            ms.ActivateSpawner();
         }

         sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.GREEN + "All spawners have been activated!");
      } else if (!name.contains("*") && !name.contains("?")) {
         MythicSpawner ms = MythicBukkit.inst().getSpawnerManager().getSpawnerByName(name);
         if (ms == null) {
            sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "The spawner entered does not exist!");
         }

         ms.ActivateSpawner();
         sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.GREEN + "Spawners " + name + " has been activated!");
      } else {
         for (MythicSpawner ms : MythicBukkit.inst().getSpawnerManager().getSpawners()) {
            if (ms.getName().matches(name.replace("?", ".?").replace("*", ".*?"))) {
               ms.ActivateSpawner();
            }
         }

         sender.sendMessage(ChatColor.GOLD + "[MythicMobs] Spawner matching pattern " + name + " have been activated!");
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(args[0], this.getPlugin().getSpawnerManager().getSpawners().stream().map(s -> s.getInternalName()).toList(), 2)
         : Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.activate";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "activate";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"a"};
   }
}
