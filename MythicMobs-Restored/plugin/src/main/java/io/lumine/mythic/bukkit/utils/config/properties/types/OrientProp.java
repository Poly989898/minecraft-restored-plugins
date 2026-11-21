package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Direction;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import io.lumine.mythic.bukkit.utils.serialize.Orient;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class OrientProp extends PropertyType<Orient> {
   private final Orient defaultValue;

   public OrientProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = Orient.of(Locus.of(0.0, 0.0, 0.0), Direction.of(0.0F, 0.0F));
   }

   public OrientProp(LuminePlugin plugin, Object file, String node, Orient defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected Orient compute(ConfigurationSection section, String node) {
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
               float yaw;
               if (split.length > 3) {
                  yaw = Float.valueOf(split[3]);
               } else {
                  yaw = 0.0F;
               }

               float pitch;
               if (split.length > 4) {
                  pitch = Float.valueOf(split[4]);
               } else {
                  pitch = 0.0F;
               }

               return Orient.of(Locus.of(x, y, z), Direction.of(yaw, pitch));
            } catch (Error | Exception e) {
               e.printStackTrace();
               return null;
            }
         }
      }
   }

   public void set(String holder, Orient value) {
      if (this.config != null) {
         String val = value.getLocus().getX()
            + ","
            + value.getLocus().getY()
            + ","
            + value.getLocus().getZ()
            + ","
            + value.getDirection().getYaw()
            + ","
            + value.getDirection().getPitch();
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

   public Orient getDefaultValue() {
      return this.defaultValue;
   }
}
