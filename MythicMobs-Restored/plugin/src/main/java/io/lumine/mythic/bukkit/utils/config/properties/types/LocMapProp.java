package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class LocMapProp extends PropertyType<Map<String, Locus>> {
   public LocMapProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected Map<String, Locus> compute(ConfigurationSection section, String node) {
      Map<String, Locus> result = new HashMap<>();
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
                  String[] split = prop.split(",");
                  double x = Double.valueOf(split[0]);
                  double y = Double.valueOf(split[1]);
                  double z = Double.valueOf(split[2]);
                  result.put(key, Locus.of(x, y, z));
               } catch (Error | Exception e) {
                  e.printStackTrace();
               }
            }

            return result;
         }
      }
   }

   protected Map<String, Locus> getDefaultValue() {
      return Collections.emptyMap();
   }
}
