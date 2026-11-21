package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ColorProp extends PropertyType<Chroma> {
   private final Chroma defaultValue;

   public ColorProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = Chroma.of(255, 255, 255);
   }

   public ColorProp(LuminePlugin plugin, Object file, String node, Chroma defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected Chroma compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else {
         String prop = section.getString(node);
         if (prop == null) {
            return this.defaultValue;
         } else {
            try {
               return Chroma.of(prop);
            } catch (Error | Exception e) {
               e.printStackTrace();
               return null;
            }
         }
      }
   }

   public void set(String holder, Chroma value) {
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

   public Chroma getDefaultValue() {
      return this.defaultValue;
   }
}
