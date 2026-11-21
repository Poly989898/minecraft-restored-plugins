package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Direction;
import io.lumine.mythic.bukkit.utils.serialize.Point;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PointListProp extends PropertyType<List<Point>> {
   public PointListProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected List<Point> compute(ConfigurationSection section, String node) {
      List<Point> result = new ArrayList<>();
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
                  String world = split[0];
                  double x = Double.valueOf(split[1]);
                  double y = Double.valueOf(split[2]);
                  double z = Double.valueOf(split[3]);
                  float yaw = split.length > 3 ? Float.valueOf(split[4]) : 0.0F;
                  float pitch = split.length > 4 ? Float.valueOf(split[5]) : 0.0F;
                  result.add(Point.of(Position.of(x, y, z, world), Direction.of(yaw, pitch)));
               } catch (Error | Exception e) {
                  e.printStackTrace();
               }
            }

            return result;
         }
      }
   }

   protected List<Point> getDefaultValue() {
      return Collections.emptyList();
   }

   public void set(String holder, List<Point> value) {
      if (this.config != null) {
         List<String> out = new ArrayList<>();

         for (Point point : value) {
            out.add(point.serializeShortForm());
         }

         if (holder != null) {
            this.config.set(holder + "." + this.node, out);
         } else {
            this.config.set(this.node, out);
         }

         try {
            ((FileConfiguration)this.config).save(this.plugin.getPropertyFile(this.file));
         } catch (Error | Exception e) {
            e.printStackTrace();
         }
      }
   }
}
