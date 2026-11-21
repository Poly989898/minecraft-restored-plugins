package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Direction;
import io.lumine.mythic.bukkit.utils.serialize.Point;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PointProp extends PropertyType<Point> {
   private final Point defaultValue;

   public PointProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = Point.of(Position.of(0.0, 0.0, 0.0, "world"), Direction.of(0.0F, 0.0F));
   }

   public PointProp(LuminePlugin plugin, Object file, String node, Point defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected Point compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else {
         String prop = section.getString(node);
         if (prop == null) {
            return this.defaultValue;
         } else {
            try {
               return Point.deserialize(prop);
            } catch (Error | Exception e) {
               e.printStackTrace();
               return null;
            }
         }
      }
   }

   public void set(String holder, Point value) {
      if (this.config != null) {
         String val = value.serializeShortForm();
         if (holder != null) {
            this.config.set(holder + "." + this.node, val);
         } else {
            this.config.set(this.node, val);
         }

         try {
            ((FileConfiguration)this.config).save(this.plugin.getPropertyFile(this.file));
         } catch (Error | Exception e) {
            e.printStackTrace();
         }
      }
   }

   public Point getDefaultValue() {
      return this.defaultValue;
   }
}
