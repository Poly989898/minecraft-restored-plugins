package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class Node2StringProp extends PropertyType<Map<String, String>> {
   public Node2StringProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected Map<String, String> compute(ConfigurationSection section, String node) {
      if (section != null && section.isSet(node)) {
         ConfigurationSection subSection = section.getConfigurationSection(node);
         if (subSection == null) {
            return Collections.emptyMap();
         } else {
            Map<String, String> ret = new HashMap<>();

            for (String key : subSection.getKeys(false)) {
               if (subSection.isString(key)) {
                  ret.put(key, subSection.getString(key));
               }
            }

            return ret;
         }
      } else {
         return Collections.emptyMap();
      }
   }

   protected Map<String, String> getDefaultValue() {
      return Collections.emptyMap();
   }
}
