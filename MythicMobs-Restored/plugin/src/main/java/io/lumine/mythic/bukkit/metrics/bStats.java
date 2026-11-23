package io.lumine.mythic.bukkit.metrics;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.WorldScaling;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public class bStats {
   public bStats(MythicBukkit plugin) {
      try {
         Metrics metrics = new Metrics(plugin, 2981);
         metrics.addCustomChart(new SimplePie("mob_types", () -> {
            int count = plugin.getMobManager().getMobTypes().size();
            if (count <= 0) {
               return "0";
            } else if (count <= 10) {
               return "1 to 10";
            } else if (count <= 25) {
               return "10 to 25";
            } else if (count <= 50) {
               return "25 to 50";
            } else if (count <= 100) {
               return "50 to 100";
            } else if (count <= 150) {
               return "100 to 150";
            } else if (count <= 200) {
               return "150 to 200";
            } else if (count <= 250) {
               return "200 to 250";
            } else if (count <= 500) {
               return "250 to 500";
            } else if (count <= 1000) {
               return "500 to 750";
            } else {
               return count <= 750 ? "750 to 1000" : "1000+";
            }
         }));
         metrics.addCustomChart(new SimplePie("skills", () -> {
            int count = plugin.getSkillManager().getSkills().size();
            if (count <= 0) {
               return "0";
            } else if (count <= 5) {
               return "1 to 5";
            } else if (count <= 10) {
               return "5 to 10";
            } else if (count <= 25) {
               return "10 to 25";
            } else if (count <= 50) {
               return "25 to 50";
            } else if (count <= 100) {
               return "50 to 100";
            } else if (count <= 150) {
               return "100 to 150";
            } else if (count <= 250) {
               return "150 to 250";
            } else if (count <= 500) {
               return "250 to 500";
            } else if (count <= 1000) {
               return "500 to 750";
            } else {
               return count <= 750 ? "750 to 1000" : "1000+";
            }
         }));
         metrics.addCustomChart(new SimplePie("item_types", () -> {
            int count = plugin.getItemManager().getItems().size();
            if (count <= 0) {
               return "0";
            } else if (count <= 5) {
               return "1 to 5";
            } else if (count <= 10) {
               return "5 to 10";
            } else if (count <= 25) {
               return "10 to 25";
            } else if (count <= 50) {
               return "25 to 50";
            } else if (count <= 100) {
               return "50 to 100";
            } else if (count <= 150) {
               return "100 to 150";
            } else if (count <= 250) {
               return "150 to 250";
            } else if (count <= 500) {
               return "250 to 500";
            } else if (count <= 1000) {
               return "500 to 750";
            } else {
               return count <= 750 ? "750 to 1000" : "1000+";
            }
         }));
         metrics.addCustomChart(new SimplePie("droptables", () -> {
            int count = plugin.getDropManager().getDropTables().size();
            if (count <= 0) {
               return "0";
            } else if (count <= 5) {
               return "1 to 5";
            } else if (count <= 10) {
               return "5 to 10";
            } else if (count <= 25) {
               return "10 to 25";
            } else if (count <= 50) {
               return "25 to 50";
            } else if (count <= 100) {
               return "50 to 100";
            } else if (count <= 150) {
               return "100 to 150";
            } else if (count <= 250) {
               return "150 to 250";
            } else if (count <= 500) {
               return "250 to 500";
            } else if (count <= 1000) {
               return "500 to 750";
            } else {
               return count <= 750 ? "750 to 1000" : "1000+";
            }
         }));
         metrics.addCustomChart(new SimplePie("spawners", () -> {
            int count = plugin.getSpawnerManager().getSpawners().size();
            if (count <= 0) {
               return "0";
            } else if (count <= 5) {
               return "1 to 5";
            } else if (count <= 10) {
               return "5 to 10";
            } else if (count <= 25) {
               return "10 to 25";
            } else if (count <= 50) {
               return "25 to 50";
            } else if (count <= 100) {
               return "50 to 100";
            } else if (count <= 150) {
               return "100 to 150";
            } else if (count <= 250) {
               return "150 to 250";
            } else if (count <= 500) {
               return "250 to 500";
            } else if (count <= 1000) {
               return "500 to 750";
            } else {
               return count <= 750 ? "750 to 1000" : "1000+";
            }
         }));
         metrics.addCustomChart(new SimplePie("random_spawns", () -> {
            int count = plugin.getRandomSpawningManager().getNumberOfSpawners();
            if (count <= 0) {
               return "0";
            } else if (count <= 5) {
               return "1 to 5";
            } else if (count <= 10) {
               return "5 to 10";
            } else if (count <= 25) {
               return "10 to 25";
            } else if (count <= 50) {
               return "25 to 50";
            } else if (count <= 100) {
               return "50 to 100";
            } else if (count <= 150) {
               return "100 to 150";
            } else if (count <= 250) {
               return "150 to 250";
            } else if (count <= 500) {
               return "250 to 500";
            } else if (count <= 1000) {
               return "500 to 750";
            } else {
               return count <= 750 ? "750 to 1000" : "1000+";
            }
         }));
         metrics.addCustomChart(new SimplePie("uses_rsp_generator", () -> plugin.getConfiguration().isRandomSpawningGenerationEnabled() ? "Yes" : "No"));
         metrics.addCustomChart(new SimplePie("uses_world_scaling", () -> WorldScaling.worldSettings.size() > 0 ? "Yes" : "No"));
         metrics.addCustomChart(new SimplePie("clock_speed", () -> String.valueOf(plugin.getConfiguration().getClockIntervalMain())));
         String devBuilds = plugin.getDescription().getVersion().contains("SNAPSHOT") ? "Yes" : "No";
         String preBuilds = MythicBukkit.isVolatile() ? "Yes" : "No";
         metrics.addCustomChart(new SimplePie("pluginVersion", () -> plugin.getVersion()));
         metrics.addCustomChart(new SimplePie("version", () -> plugin.getVersion()));
         metrics.addCustomChart(new SimplePie("premium", () -> preBuilds));
         metrics.addCustomChart(new SimplePie("devbuilds", () -> devBuilds));
         MythicLogger.log("Started up bStats Metrics");
      } catch (Exception e) {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Metrics: Failed to enable bStats Metrics stats.");
      }
   }
}
