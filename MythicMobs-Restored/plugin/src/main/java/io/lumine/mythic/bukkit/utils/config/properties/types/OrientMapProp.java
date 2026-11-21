package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Direction;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import io.lumine.mythic.bukkit.utils.serialize.Orient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.configuration.ConfigurationSection;

public class OrientMapProp extends PropertyType<Map<String, Orient>> {
   public OrientMapProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected Map<String, Orient> compute(ConfigurationSection section, String node) {
      Map<String, Orient> result = new HashMap<>();
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
                  float yaw = Float.valueOf(split[3]);
                  float pitch = Float.valueOf(split[4]);
                  result.put(key, Orient.of(Locus.of(x, y, z), Direction.of(yaw, pitch)));
               } catch (Error | Exception e) {
                  e.printStackTrace();
               }
            }

            return result;
         }
      }
   }

   protected Map<String, Orient> getDefaultValue() {
      return Collections.emptyMap();
   }

   public void set(String holder, Map<String, Orient> value) {
      if (this.config != null) {
         List<String> out = new ArrayList<>();

         for (Entry<String, Orient> entry : value.entrySet()) {
            Orient orient = entry.getValue();
            String o = entry.getKey()
               + " "
               + orient.getLocus().getX()
               + ","
               + orient.getLocus().getY()
               + ","
               + orient.getLocus().getZ()
               + ","
               + orient.getDirection().getYaw()
               + ","
               + orient.getDirection().getPitch();
            out.add(o);
         }

         this.config.set(this.node, out);
      }
   }
}
