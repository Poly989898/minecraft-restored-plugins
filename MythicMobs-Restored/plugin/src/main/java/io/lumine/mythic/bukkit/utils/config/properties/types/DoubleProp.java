package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import org.bukkit.configuration.ConfigurationSection;

public class DoubleProp extends PropertyType<Double> {
   private final Double defaultValue;

   public DoubleProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = 0.0;
   }

   public DoubleProp(LuminePlugin plugin, Object file, String node, double defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected Double compute(ConfigurationSection section, String node) {
      return section == null ? this.defaultValue : section.getDouble(node, this.defaultValue);
   }

   public Double getDefaultValue() {
      return this.defaultValue;
   }
}
