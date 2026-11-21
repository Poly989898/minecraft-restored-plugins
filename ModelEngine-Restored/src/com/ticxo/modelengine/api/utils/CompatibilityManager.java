package com.ticxo.modelengine.api.utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityManager implements Listener {
   private final PluginManager pluginManager;
   private final Set<String> allPlugins = Sets.newConcurrentHashSet();
   private final Map<String, MemoizeSupplier<CompatibilityManager.CompatibilityConfiguration>> compatibilities = Maps.newConcurrentMap();

   public CompatibilityManager(JavaPlugin plugin) {
      this.pluginManager = Bukkit.getPluginManager();

      for (Plugin pluginManagerPlugin : this.pluginManager.getPlugins()) {
         this.allPlugins.add(pluginManagerPlugin.getName());
      }

      this.pluginManager.registerEvents(this, plugin);
   }

   public void registerSupport(String pluginName, Supplier<CompatibilityManager.CompatibilityConfiguration> configuration) {
      if (this.allPlugins.contains(pluginName)) {
         Plugin plugin = this.pluginManager.getPlugin(pluginName);
         MemoizeSupplier<CompatibilityManager.CompatibilityConfiguration> memoize = MemoizeSupplier.of(configuration);
         if (plugin != null && plugin.isEnabled() && memoize.get().tryApply(plugin)) {
            TLogger.log("Compatibility applied: " + plugin.getName());
         } else {
            this.compatibilities.put(pluginName, memoize);
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onPluginLoad(PluginEnableEvent event) {
      Plugin plugin = event.getPlugin();
      if (plugin.isEnabled()) {
         MemoizeSupplier<CompatibilityManager.CompatibilityConfiguration> config = this.compatibilities.get(plugin.getName());
         if (config != null) {
            if (!config.get().tryApply(plugin)) {
               TLogger.error("Failed to apply compatibility support for " + plugin.getName() + ".");
            } else {
               TLogger.log("Compatibility applied: " + plugin.getName());
            }
         }
      }
   }

   @FunctionalInterface
   public interface CompatibilityConfiguration {
      boolean tryApply(Plugin plugin);
   }
}
