package io.lumine.mythic.lib.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.comp.mythicmobs.MythicMobsHealIndicators;
import io.lumine.mythic.lib.listener.option.DamageIndicators;
import io.lumine.mythic.lib.listener.option.RegenIndicators;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class IndicatorManager {
   private final List<Listener> indicatorsListeners = new ArrayList<>();

   public void load(FileConfiguration configuration) {
      PluginManager manager = Bukkit.getPluginManager();
      MythicLib plugin = MythicLib.plugin;
      if (configuration.getBoolean("game-indicators.damage.enabled")) {
         try {
            Listener damageIndicators = new DamageIndicators(configuration.getConfigurationSection("game-indicators.damage"));
            manager.registerEvents(damageIndicators, plugin);
            this.indicatorsListeners.add(damageIndicators);
         } catch (RuntimeException exception) {
            plugin.getLogger().log(Level.WARNING, "Could not load damage indicators: " + exception.getMessage());
         }
      }

      if (configuration.getBoolean("game-indicators.regen.enabled")) {
         RegenIndicators regenIndicators = new RegenIndicators(configuration.getConfigurationSection("game-indicators.regen"));
         manager.registerEvents(regenIndicators, plugin);
         this.indicatorsListeners.add(regenIndicators);
         if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            MythicMobsHealIndicators mmIndicators = new MythicMobsHealIndicators(regenIndicators);
            manager.registerEvents(mmIndicators, plugin);
            this.indicatorsListeners.add(mmIndicators);
         }
      }
   }

   public void reload(FileConfiguration configuration) {
      this.indicatorsListeners.forEach(HandlerList::unregisterAll);
      this.indicatorsListeners.clear();
      this.load(configuration);
   }
}
