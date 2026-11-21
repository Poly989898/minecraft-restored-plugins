package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class LocProp extends PropertyType<Locus> {
   private final Locus defaultValue;

   public LocProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = Locus.of(0.0, 0.0, 0.0);
   }

   public LocProp(LuminePlugin plugin, Object file, String node, Locus defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected Locus compute(ConfigurationSection section, String node) {
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
               return Locus.of(x, y, z);
            } catch (Error | Exception e) {
               e.printStackTrace();
               return null;
            }
         }
      }
   }

   public void set(String holder, Locus value) {
      if (this.config != null) {
         String val = value.getX() + "," + value.getY() + "," + value.getZ();
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

   public Locus getDefaultValue() {
      return this.defaultValue;
   }
}
