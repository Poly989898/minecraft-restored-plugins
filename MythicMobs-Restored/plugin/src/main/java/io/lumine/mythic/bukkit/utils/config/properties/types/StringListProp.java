package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;

public class StringListProp extends PropertyType<List<String>> {
   private final List<String> defaultValue;

   public StringListProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = new ArrayList<>();
   }

   public StringListProp(LuminePlugin plugin, Object file, String node, List<String> defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected List<String> compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else if (!section.isSet(node)) {
         return this.defaultValue;
      } else {
         List<String> prop = section.getStringList(node);
         return prop == null ? this.defaultValue : section.getStringList(node);
      }
   }

   public List<String> getDefaultValue() {
      return this.defaultValue;
   }
}
