package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import org.bukkit.configuration.ConfigurationSection;

public class IntProp extends PropertyType<Integer> {
   private final Integer defaultValue;

   public IntProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = 0;
   }

   public IntProp(LuminePlugin plugin, Object file, String node, int defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   public Integer compute(ConfigurationSection section, String value) {
      return section == null ? this.defaultValue : section.getInt(value, this.defaultValue);
   }

   public Integer getDefaultValue() {
      return this.defaultValue;
   }
}
