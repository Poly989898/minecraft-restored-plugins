package io.lumine.mythic.lib.util.configobject;

import java.util.Objects;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;

public class ConfigSectionObject implements ConfigObject {
   private final ConfigurationSection config;

   public ConfigSectionObject(ConfigurationSection config) {
      this.config = config;
   }

   @Override
   public String getString(String key) {
      return Objects.requireNonNull(this.config.getString(key), "Could not find string with key '" + key + "'");
   }

   @Override
   public String getString(String key, String defaultValue) {
      return this.config.getString(key, Objects.requireNonNull(defaultValue, "Default value cannot be null"));
   }

   @Override
   public double getDouble(String key) {
      return this.config.getDouble(key);
   }

   @Override
   public double getDouble(String key, double defaultValue) {
      return this.config.getDouble(key, defaultValue);
   }

   @Override
   public int getInt(String key) {
      return this.config.getInt(key);
   }

   @Override
   public int getInt(String key, int defaultValue) {
      return this.config.getInt(key, defaultValue);
   }

   @Override
   public boolean getBoolean(String key) {
      return this.config.getBoolean(key);
   }

   @Override
   public boolean getBoolean(String key, boolean defaultValue) {
      return this.config.getBoolean(key, defaultValue);
   }

   @NotNull
   public ConfigSectionObject getObject(String key) {
      return new ConfigSectionObject(Objects.requireNonNull(this.config.getConfigurationSection(key), "Could not find section with key '" + key + "'"));
   }

   @NotNull
   public ConfigSectionObject adaptObject(String key) {
      Object found = this.config.get(key);
      ConfigurationSection loadFrom;
      if (found instanceof ConfigurationSection) {
         loadFrom = (ConfigurationSection)found;
      } else {
         if (!(found instanceof String)) {
            throw new IllegalArgumentException("Expecting either a string or object");
         }

         loadFrom = new MemoryConfiguration();
         loadFrom.set("type", found);
      }

      return new ConfigSectionObject(loadFrom);
   }

   @Override
   public boolean contains(String key) {
      return this.config.contains(key);
   }

   @NotNull
   @Override
   public Set<String> getKeys() {
      return this.config.getKeys(false);
   }

   @Override
   public String getKey() {
      return this.config.getName();
   }
}
