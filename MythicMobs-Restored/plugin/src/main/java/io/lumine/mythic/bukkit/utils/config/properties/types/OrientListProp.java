package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Direction;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import io.lumine.mythic.bukkit.utils.serialize.Orient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;

public class OrientListProp extends PropertyType<List<Orient>> {
   public OrientListProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected List<Orient> compute(ConfigurationSection section, String node) {
      List<Orient> result = new ArrayList<>();
      if (section == null) {
         return result;
      } else {
         List<String> listprop = section.getStringList(node);
         if (listprop == null) {
            return result;
         } else {
            for (String prop : listprop) {
               try {
                  String[] split = prop.split(",");
                  double x = Double.valueOf(split[0]);
                  double y = Double.valueOf(split[1]);
                  double z = Double.valueOf(split[2]);
                  float yaw = split.length > 3 ? Float.valueOf(split[3]) : 0.0F;
                  float pitch = split.length > 4 ? Float.valueOf(split[4]) : 0.0F;
                  result.add(Orient.of(Locus.of(x, y, z), Direction.of(yaw, pitch)));
               } catch (Error | Exception e) {
                  e.printStackTrace();
               }
            }

            return result;
         }
      }
   }

   protected List<Orient> getDefaultValue() {
      return Collections.emptyList();
   }

   public void set(String holder, List<Orient> value) {
      if (this.config != null) {
         List<String> out = new ArrayList<>();

         for (Orient orient : value) {
            String o = orient.getLocus().getX()
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
