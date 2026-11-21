package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.spawning.spawners.SpawnerAttribute;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SetCommand extends Command<MythicBukkit> {
   public SetCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage(ColorString.get("&6--====|||| &c&lMythicMobs &6||||====--"));
         sender.sendMessage(ChatColor.YELLOW + "Command: " + ChatColor.AQUA + "/mm spawners set [name] [attribute] [value]");
         sender.sendMessage("" + ChatColor.YELLOW + ChatColor.BOLD + "Available Attributes:");
         sender.sendMessage(
            ChatColor.GOLD + "activationrange" + ChatColor.GREEN + ChatColor.ITALIC + " - The max range a player can be for the spawner to activate."
         );
         sender.sendMessage(
            ChatColor.GOLD + "chance" + ChatColor.GREEN + ChatColor.ITALIC + " - (0.0-1.0) The chance for the spawner to spawn when it would otherwise spawn."
         );
         sender.sendMessage(ChatColor.GOLD + "cooldown" + ChatColor.GREEN + ChatColor.ITALIC + " - The time (in seconds) between mob spawns.");
         sender.sendMessage(ChatColor.GOLD + "group" + ChatColor.GREEN + ChatColor.ITALIC + " - The name of a group to organize the spawner into.");
         sender.sendMessage(
            ChatColor.GOLD
               + "healonleash"
               + ChatColor.GREEN
               + ChatColor.ITALIC
               + " - (true/false) Whether or not a mob will heal to full health upon being leashed."
         );
         sender.sendMessage(
            ChatColor.GOLD
               + "leashrange"
               + ChatColor.GREEN
               + ChatColor.ITALIC
               + " - The max range a mob can be from the spawner before it is teleported back (0=none)."
         );
         sender.sendMessage(
            ChatColor.GOLD + "maxmobs" + ChatColor.GREEN + ChatColor.ITALIC + " - The maximum number of mobs this spawner can have active at once."
         );
         sender.sendMessage(ChatColor.GOLD + "moblevel" + ChatColor.GREEN + ChatColor.ITALIC + " - The level of the mobs spawned by the spawner.");
         sender.sendMessage(ChatColor.GOLD + "mobsperspawn" + ChatColor.GREEN + ChatColor.ITALIC + " - How many mobs will spawn per spawner cycle.");
         sender.sendMessage(
            ChatColor.GOLD + "randomrotation" + ChatColor.GREEN + ChatColor.ITALIC + " - (true/false) Whether mobs spawn with a random rotation."
         );
         sender.sendMessage(
            ChatColor.GOLD
               + "resetthreatonleash"
               + ChatColor.GREEN
               + ChatColor.ITALIC
               + " - (true/false) Whether or not the mob's target should be reset upon being leashed."
         );
         sender.sendMessage(
            ChatColor.GOLD + "showflames" + ChatColor.GREEN + ChatColor.ITALIC + " - (true/false) whether to always show spawner flames on the block."
         );
         sender.sendMessage(
            ChatColor.GOLD + "useworldscaling" + ChatColor.GREEN + ChatColor.ITALIC + " - (true/false) whether world scaling affects the spawned mob's level."
         );
         sender.sendMessage(
            ChatColor.GOLD + "usetimer" + ChatColor.GREEN + ChatColor.ITALIC + " - (true/false) whether the spawner should spawn on a timer or not.."
         );
         sender.sendMessage(
            ChatColor.GOLD
               + "warmup"
               + ChatColor.GREEN
               + ChatColor.ITALIC
               + " - The time (in seconds) until a mob spawns after a mob dies, when the max number of mobs has been reached."
         );
         return true;
      } else {
         String name = args[0];
         String option = args[1];
         String value = "";

         for (int i = 2; i < args.length; i++) {
            value = value.concat(args[i]) + " ";
         }

         if (name.startsWith("g:")) {
            String group = name.substring(2);

            for (MythicSpawner ms : MythicBukkit.inst().getSpawnerManager().getSpawnersByGroup(group)) {
               if (!MythicBukkit.inst().getSpawnerManager().setSpawnerAttribute(ms, option, value)) {
                  sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "The attribute or value you entered was invalid!");
                  return true;
               }
            }

            sender.sendMessage(
               ChatColor.GOLD
                  + "[MythicMobs] "
                  + ChatColor.GREEN
                  + "Spawner attribute "
                  + ChatColor.AQUA
                  + option
                  + ChatColor.GREEN
                  + " set to "
                  + ChatColor.AQUA
                  + value
                  + ChatColor.GREEN
                  + " on group "
                  + group
            );
         } else if (name.equals("*")) {
            for (MythicSpawner msx : MythicBukkit.inst().getSpawnerManager().getSpawners()) {
               if (!MythicBukkit.inst().getSpawnerManager().setSpawnerAttribute(msx, option, value)) {
                  sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "The condition you entered was invalid!");
                  return true;
               }
            }

            sender.sendMessage(
               ChatColor.GOLD
                  + "[MythicMobs] Spawner attribute "
                  + ChatColor.AQUA
                  + option
                  + ChatColor.GREEN
                  + " set to "
                  + ChatColor.AQUA
                  + value
                  + ChatColor.GREEN
                  + " on all spawners!"
            );
         } else if (!name.contains("*") && !name.contains("?")) {
            MythicSpawner msxx = MythicBukkit.inst().getSpawnerManager().getSpawnerByName(name);
            if (msxx == null) {
               sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "You must enter a valid Mythic Spawner. That one was not found!");
               return true;
            }

            if (!MythicBukkit.inst().getSpawnerManager().setSpawnerAttribute(msxx, option, value)) {
               sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "The attribute or value you entered was invalid!");
            } else {
               sender.sendMessage(
                  ChatColor.GOLD
                     + "[MythicMobs] "
                     + ChatColor.GREEN
                     + "Spawner attribute "
                     + ChatColor.AQUA
                     + option
                     + ChatColor.GREEN
                     + " set to "
                     + ChatColor.AQUA
                     + value
                     + " on spawner "
                     + name
               );
            }
         } else {
            for (MythicSpawner msxxx : MythicBukkit.inst().getSpawnerManager().getSpawners()) {
               if (msxxx.getName().matches(name.replace("?", ".?").replace("*", ".*?"))
                  && !MythicBukkit.inst().getSpawnerManager().setSpawnerAttribute(msxxx, option, value)) {
                  sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "The condition you entered was invalid!");
                  return true;
               }
            }

            sender.sendMessage(
               ChatColor.GOLD
                  + "[MythicMobs] Spawner attribute "
                  + ChatColor.AQUA
                  + option
                  + ChatColor.GREEN
                  + " set to "
                  + ChatColor.AQUA
                  + value
                  + ChatColor.GREEN
                  + " on spawners matching pattern "
                  + name
                  + "!"
            );
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         return CollectionUtils.filterMatches(args[0], this.getPlugin().getSpawnerManager().getSpawners().stream().map(s -> s.getInternalName()).toList(), 2);
      } else if (args.length == 2) {
         List<String> attributes = Arrays.stream(SpawnerAttribute.values()).map(a -> a.toString().toLowerCase()).toList();
         return CollectionUtils.filterMatches(args[1], attributes, 2);
      } else {
         return Collections.emptyList();
      }
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.set";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "set";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"s"};
   }
}
