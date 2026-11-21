package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;

public class LocListProp extends PropertyType<List<Locus>> {
   public LocListProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected List<Locus> compute(ConfigurationSection section, String node) {
      List<Locus> result = new ArrayList<>();
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
                  result.add(Locus.of(x, y, z));
               } catch (Error | Exception e) {
                  e.printStackTrace();
               }
            }

            return result;
         }
      }
   }

   protected List<Locus> getDefaultValue() {
      return Collections.emptyList();
   }

   public void set(String holder, List<Locus> value) {
      if (this.config != null) {
         List<String> out = new ArrayList<>();

         for (Locus locus : value) {
            String o = locus.getX() + "," + locus.getY() + "," + locus.getZ();
            out.add(o);
         }

         this.config.set(this.node, out);
      }
   }
}
