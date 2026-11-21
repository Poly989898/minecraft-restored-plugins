package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigurationSectionProp extends PropertyType<ConfigurationSection> {
   public ConfigurationSectionProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected ConfigurationSection compute(ConfigurationSection section, String node) {
      return section != null && section.isConfigurationSection(node) ? section.getConfigurationSection(node) : null;
   }

   protected ConfigurationSection getDefaultValue() {
      return null;
   }
}
