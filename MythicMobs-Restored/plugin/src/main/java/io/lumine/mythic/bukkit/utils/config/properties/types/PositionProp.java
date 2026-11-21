package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PositionProp extends PropertyType<Position> {
   private final Position defaultValue;

   public PositionProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = Position.of(0.0, 0.0, 0.0, "world");
   }

   public PositionProp(LuminePlugin plugin, Object file, String node, Position defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected Position compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else {
         String prop = section.getString(node);
         if (prop == null) {
            return this.defaultValue;
         } else {
            try {
               String[] split = prop.split(",");
               double x = Double.valueOf(split[0]);
               double y = Double.valueOf(split[1]);
               double z = Double.valueOf(split[2]);
               String world = split[3];
               return Position.of(x, y, z, world);
            } catch (Error | Exception e) {
               e.printStackTrace();
               return null;
            }
         }
      }
   }

   public void set(String holder, Position value) {
      if (this.config != null) {
         String val = value.getX() + "," + value.getY() + "," + value.getZ() + "," + value.getWorld();
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

   public Position getDefaultValue() {
      return this.defaultValue;
   }
}
