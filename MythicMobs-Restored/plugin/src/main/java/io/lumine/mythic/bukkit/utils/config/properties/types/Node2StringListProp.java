package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class Node2StringListProp extends PropertyType<Map<String, List<String>>> {
   public Node2StringListProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected Map<String, List<String>> compute(ConfigurationSection section, String node) {
      if (section != null && section.isSet(node)) {
         ConfigurationSection subSection = section.getConfigurationSection(node);
         if (subSection == null) {
            return Collections.emptyMap();
         } else {
            Map<String, List<String>> ret = new HashMap<>();

            for (String key : subSection.getKeys(false)) {
               if (subSection.isList(key)) {
                  ret.put(key, subSection.getStringList(key));
               }
            }

            return ret;
         }
      } else {
         return Collections.emptyMap();
      }
   }

   protected Map<String, List<String>> getDefaultValue() {
      return Collections.emptyMap();
   }
}
