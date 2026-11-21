package io.lumine.mythic.bukkit.commands.debug;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TimingsCommand extends Command<MythicBukkit> {
   public TimingsCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length > 0) {
         if ("ON".equals(args[0].toUpperCase())) {
            MythicBukkit.inst().getTimingsHandler().enable();
            sender.sendMessage(ChatColor.GOLD + "[MythicMobs]" + ChatColor.GREEN + " Timings have been enabled.");
         } else if ("OFF".equals(args[0].toUpperCase())) {
            MythicBukkit.inst().getTimingsHandler().disable();
            sender.sendMessage(ChatColor.GOLD + "[MythicMobs]" + ChatColor.GREEN + " Timings have been disabled.");
         } else if ("RESET".equals(args[0].toUpperCase())) {
            MythicBukkit.inst().getTimingsHandler().reset();
            sender.sendMessage(ChatColor.GOLD + "[MythicMobs]" + ChatColor.GREEN + " Timings have been reset.");
         } else if ("SPAWNERS".equals(args[0].toUpperCase())) {
            double overallMS = MythicBukkit.inst().getTimingsHandler().getRunTimeMillis();
            double totalMS = MythicBukkit.inst().getTimingsHandler().getTimeTotal() / 1000000L;
            double totalSpawners = MythicBukkit.inst().getTimingsHandler().getTimeSpawners() / 1000000L;
            MythicLogger.log("Spawner Timings");
            MythicLogger.log("Time Analyzed: " + overallMS);
            MythicLogger.log("Clock Time: " + Math.round(totalMS * 100.0) / 100L + "ms (" + Math.round(totalMS / overallMS * 10000.0) / 100L + "%)");
            MythicLogger.log(
               "- CSpawners: "
                  + Math.round(totalSpawners * 100.0) / 100L
                  + "ms ("
                  + Math.round(totalSpawners / totalMS * 10000.0) / 100L
                  + "% Clock, "
                  + Math.round(totalSpawners / overallMS * 10000.0) / 100L
                  + "% Overall)"
            );
            MythicLogger.log("-- All Spawners:");
            HashMap<String, Long> map = (HashMap<String, Long>)MythicBukkit.inst().getTimingsHandler().getAllSpawnerTimes().clone();
            HashMap<String, Long> hashMap = sortHashMapByValuesD(map);

            for (String s : hashMap.keySet()) {
               long tM = hashMap.get(s) / 1000000L;
               long tMTS = Math.round(tM / totalSpawners * 10000.0) / 100L;
               long tMC = Math.round(tM / totalMS * 10000.0) / 100L;
               long tMO = Math.round(tM / overallMS * 10000.0) / 100L;
               MythicLogger.log(String.format("** %32s %8d ms - %4d pS %4d pC %4d pOverall)", s, tM, tMTS, tMC, tMO));
            }

            sender.sendMessage(ChatColor.GOLD + "[MythicMobs]" + ChatColor.GREEN + " Spawner timings have been pasted to the console.");
         } else {
            if (!"SKILLS".equals(args[0].toUpperCase())) {
               return false;
            }

            double overallMS = MythicBukkit.inst().getTimingsHandler().getRunTimeMillis();
            double totalMS = MythicBukkit.inst().getTimingsHandler().getTimeTotal() / 1000000L;
            double totalSkills = MythicBukkit.inst().getTimingsHandler().getTimeSkills() / 1000000L;
            MythicLogger.log(" AbstractSkill Timings");
            MythicLogger.log("Time Analyzed: " + overallMS);
            MythicLogger.log("Clock Time: " + Math.round(totalMS * 100.0) / 100L + "ms (" + Math.round(totalMS / overallMS * 10000.0) / 100L + "%)");
            MythicLogger.log(
               "- TmrSkills: "
                  + Math.round(totalSkills * 100.0) / 100L
                  + "ms ("
                  + Math.round(totalSkills / totalMS * 10000.0) / 100L
                  + "% Clock, "
                  + Math.round(totalSkills / overallMS * 10000.0) / 100L
                  + "% Overall)"
            );
            MythicLogger.log("-- All Timer Skills:");
            HashMap<String, Long> map = (HashMap<String, Long>)MythicBukkit.inst().getTimingsHandler().getAllSkillTimes().clone();
            HashMap<String, Long> hashMap2 = sortHashMapByValuesD(map);

            for (String s : hashMap2.keySet()) {
               long tM = hashMap2.get(s) / 1000000L;
               long tMTS = Math.round(tM / totalSkills * 10000.0) / 100L;
               long tMC = Math.round(tM / totalMS * 10000.0) / 100L;
               long tMO = Math.round(tM / overallMS * 10000.0) / 100L;
               MythicLogger.log(String.format("** %64s %8d ms - %4d pS %4d pC %4d pOverall)", s, tM, tMTS, tMC, tMO));
            }

            sender.sendMessage(ChatColor.GOLD + "[MythicMobs]" + ChatColor.GREEN + " AbstractSkill timings have been pasted to the console.");
         }
      } else {
         if (!MythicBukkit.inst().getTimingsHandler().isEnabled()) {
            sender.sendMessage(ChatColor.GOLD + "[MythicMobs]" + ChatColor.GREEN + " Timings must be enabled first.");
            return true;
         }

         double overallMS = MythicBukkit.inst().getTimingsHandler().getRunTimeMillis();
         double totalMS = MythicBukkit.inst().getTimingsHandler().getTimeTotal() / 1000000L;
         double totalSkills = MythicBukkit.inst().getTimingsHandler().getTimeSkills() / 1000000L;
         double totalRandomSpawners = MythicBukkit.inst().getTimingsHandler().getTimeRandomSpawners() / 1000000L;
         double totalRandomGenerators = MythicBukkit.inst().getTimingsHandler().getTimeRandomGenerators() / 1000000L;
         double totalScanner = MythicBukkit.inst().getTimingsHandler().getTimeScanner() / 1000000L;
         double totalSpawners = MythicBukkit.inst().getTimingsHandler().getTimeSpawners() / 1000000L;
         double totalThreatT = MythicBukkit.inst().getTimingsHandler().getTimeThreatTables() / 1000000L;
         double overhead = totalMS - totalSkills - totalSpawners - totalThreatT - totalScanner - totalRandomSpawners;
         sender.sendMessage("" + ChatColor.GOLD + ChatColor.BOLD + " Clock Timings");
         sender.sendMessage(ChatColor.AQUA + "Time Analyzed: " + overallMS);
         sender.sendMessage(
            ChatColor.AQUA
               + "Clock Time: "
               + ChatColor.GREEN
               + Math.round(totalMS * 100.0) / 100.0
               + "ms ("
               + Math.round(totalMS / overallMS * 10000.0) / 100.0
               + "%)"
         );
         sender.sendMessage(
            ChatColor.AQUA
               + "- Skills & Conditions: "
               + ChatColor.GREEN
               + Math.round(totalSkills * 100.0) / 100.0
               + "ms ("
               + Math.round(totalSkills / totalMS * 10000.0) / 100.0
               + "% Clock, "
               + Math.round(totalSkills / overallMS * 10000.0) / 100.0
               + "% Overall)"
         );
         sender.sendMessage(
            ChatColor.AQUA
               + "- RndSpawns(Replaced): "
               + ChatColor.GREEN
               + Math.round(totalRandomSpawners * 100.0) / 100.0
               + "ms ("
               + Math.round(totalRandomSpawners / totalMS * 10000.0) / 100.0
               + "% Clock, "
               + Math.round(totalRandomSpawners / overallMS * 10000.0) / 100.0
               + "% Overall)"
         );
         sender.sendMessage(
            ChatColor.AQUA
               + "- RndSpawns(Generate): "
               + ChatColor.GREEN
               + Math.round(totalRandomGenerators * 100.0) / 100.0
               + "ms ("
               + Math.round(totalRandomGenerators / totalMS * 10000.0) / 100.0
               + "% Clock, "
               + Math.round(totalRandomGenerators / overallMS * 10000.0) / 100.0
               + "% Overall)"
         );
         sender.sendMessage(
            ChatColor.AQUA
               + "- Static Mob Spawners: "
               + ChatColor.GREEN
               + Math.round(totalSpawners * 100.0) / 100.0
               + "ms ("
               + Math.round(totalSpawners / totalMS * 10000.0) / 100.0
               + "% Clock, "
               + Math.round(totalSpawners / overallMS * 10000.0) / 100.0
               + "% Overall)"
         );
         sender.sendMessage(
            ChatColor.AQUA
               + "- World Scanner: "
               + ChatColor.GREEN
               + Math.round(totalScanner * 100.0) / 100.0
               + "ms ("
               + Math.round(totalScanner / totalMS * 10000.0) / 100.0
               + "% Clock, "
               + Math.round(totalScanner / overallMS * 10000.0) / 100.0
               + "% Overall)"
         );
         sender.sendMessage(
            ChatColor.AQUA
               + "- Threat Tables: "
               + ChatColor.GREEN
               + Math.round(totalThreatT * 100.0) / 100.0
               + "ms ("
               + Math.round(totalThreatT / totalMS * 10000.0) / 100.0
               + "% Clock, "
               + Math.round(totalThreatT / overallMS * 10000.0) / 100.0
               + "% Overall)"
         );
         sender.sendMessage(
            ChatColor.AQUA
               + "- Overhead+: "
               + ChatColor.GREEN
               + Math.round(overhead * 100.0) / 100.0
               + "ms ("
               + Math.round(overhead / totalMS * 10000.0) / 100.0
               + "% Clock, "
               + Math.round(overhead / overallMS * 10000.0) / 100.0
               + "% Overall)"
         );
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.debug.timings";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "timings";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"t"};
   }

   private static LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
      List mapKeys = new ArrayList(passedMap.keySet());
      List mapValues = new ArrayList(passedMap.values());
      Collections.sort(mapValues);
      Collections.sort(mapKeys);
      LinkedHashMap sortedMap = new LinkedHashMap();

      for (Object val : mapValues) {
         for (Object key : mapKeys) {
            String comp1 = passedMap.get(key).toString();
            String comp2 = val.toString();
            if (comp1.equals(comp2)) {
               passedMap.remove(key);
               mapKeys.remove(key);
               sortedMap.put((String)key, (Long)val);
               break;
            }
         }
      }

      return sortedMap;
   }
}
