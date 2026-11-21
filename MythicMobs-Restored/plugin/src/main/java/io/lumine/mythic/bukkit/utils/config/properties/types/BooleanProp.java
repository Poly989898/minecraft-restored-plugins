package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import org.bukkit.configuration.ConfigurationSection;

public class BooleanProp extends PropertyType<Boolean> {
   private final Boolean defaultValue;

   public BooleanProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = false;
   }

   public BooleanProp(LuminePlugin plugin, Object file, String node, boolean defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   public Boolean compute(ConfigurationSection section, String value) {
      return section == null ? this.defaultValue : section.getBoolean(value, this.defaultValue);
   }

   public Boolean getDefaultValue() {
      return this.defaultValue;
   }
}
