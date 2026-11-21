package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import org.bukkit.configuration.ConfigurationSection;

public class StringProp extends PropertyType<String> {
   private final String defaultValue;

   public StringProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = "";
   }

   public StringProp(LuminePlugin plugin, Object file, String node, String defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected String compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else {
         String prop = section.getString(node);
         if (prop == null) {
            return this.defaultValue;
         } else {
            String value = section.getString(node);
            return value == null ? null : ColorString.get(value);
         }
      }
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }
}
