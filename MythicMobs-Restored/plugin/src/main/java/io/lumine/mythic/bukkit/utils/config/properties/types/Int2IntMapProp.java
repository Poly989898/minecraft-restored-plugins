package io.lumine.mythic.bukkit.utils.config.properties.types;

import com.google.common.collect.Maps;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class Int2IntMapProp extends PropertyType<Map<Integer, Integer>> {
   public Int2IntMapProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected Map<Integer, Integer> compute(ConfigurationSection section, String node) {
      Map<Integer, Integer> result = new HashMap<>();
      if (section == null) {
         return result;
      } else {
         section = section.getConfigurationSection(node);
         if (section == null) {
            return result;
         } else {
            for (String key : section.getKeys(false)) {
               String prop = section.getString(key);

               try {
                  int ik = Integer.parseInt(key);
                  int iv = Integer.parseInt(prop);
                  result.put(ik, iv);
               } catch (Error | Exception e) {
                  e.printStackTrace();
               }
            }

            return result;
         }
      }
   }

   protected Map<Integer, Integer> getDefaultValue() {
      return Maps.newHashMap();
   }
}
