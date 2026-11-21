package com.ticxo.modelengine.api.utils.config;

import com.google.common.collect.Sets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ConfigManager {
   private final JavaPlugin plugin;
   private final Map<String, Object> config = new LinkedHashMap<>();
   private final Set<Runnable> updater = Sets.newConcurrentHashSet();
   private FileConfiguration file;

   public ConfigManager(JavaPlugin plugin) {
      this.plugin = plugin;
      this.file = plugin.getConfig();
      plugin.saveDefaultConfig();
   }

   public void register(Property property) {
      if (property.getDef() != null) {
         this.register(property.getPath(), property.getDef());
      }
   }

   public void register(String path, @NotNull Object def) {
      this.config.put(path, this.file.get(path, def));
   }

   public void registerReferenceUpdate(Runnable runnable) {
      this.updater.add(runnable);
      runnable.run();
   }

   public void reload() {
      this.plugin.reloadConfig();
      this.file = this.plugin.getConfig();
      this.config.replaceAll((p, v) -> this.file.get(p, this.config.get(p)));
   }

   public void updateReferences() {
      this.updater.forEach(Runnable::run);
   }

   public <T> T get(String path) {
      try {
         return (T)this.config.get(path);
      } catch (ClassCastException ignored) {
         return null;
      }
   }

   public <T> T get(Property property) {
      return this.get(property.getPath());
   }

   public int getInt(Property property) {
      return this.<Integer>get(property);
   }

   public double getDouble(Property property) {
      return this.get(property) instanceof Number number ? number.doubleValue() : 0.0;
   }

   public String getString(Property property) {
      return this.get(property);
   }

   public boolean getBoolean(Property property) {
      return this.<Boolean>get(property);
   }

   public List<String> getStringList(Property property) {
      return this.get(property);
   }

   public ConfigurationSection getConfigSection(String path) {
      return this.file.getConfigurationSection(path);
   }

   public void set(Property property, Object value) {
      this.config.put(property.getPath(), value);
   }

   public void save() {
      this.config.forEach(this.file::set);
      this.plugin.saveConfig();
   }
}
