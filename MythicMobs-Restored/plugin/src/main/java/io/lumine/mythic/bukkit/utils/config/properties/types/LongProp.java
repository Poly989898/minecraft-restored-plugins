package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import org.bukkit.configuration.ConfigurationSection;

public class LongProp extends PropertyType<Long> {
   private final Long defaultValue;

   public LongProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = 0L;
   }

   public LongProp(LuminePlugin plugin, Object file, String node, long defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   public Long compute(ConfigurationSection section, String value) {
      return section == null ? this.defaultValue : section.getLong(value, this.defaultValue);
   }

   public Long getDefaultValue() {
      return this.defaultValue;
   }
}
