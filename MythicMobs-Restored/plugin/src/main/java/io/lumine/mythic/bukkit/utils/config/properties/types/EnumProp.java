package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class EnumProp<T extends Enum<T>> extends PropertyType<T> {
   private final Class<T> clazz;
   private final T defaultValue;

   public EnumProp(LuminePlugin plugin, Object file, Class<T> clazz, String node) {
      super(plugin, file, node);
      this.clazz = clazz;
      this.defaultValue = null;
   }

   public EnumProp(LuminePlugin plugin, Object file, Class<T> clazz, String node, T defaultValue) {
      super(plugin, file, node);
      this.clazz = clazz;
      this.defaultValue = defaultValue;
   }

   protected T compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else {
         try {
            T value = Enum.valueOf(this.clazz, section.getString(node));
            return value == null ? this.defaultValue : value;
         } catch (Error | Exception e) {
            return this.defaultValue;
         }
      }
   }

   public void set(String holder, T value) {
      if (this.config != null) {
         if (holder != null) {
            this.config.set(holder + "." + this.node, value.toString());
         } else {
            this.config.set(this.node, value.toString());
         }

         try {
            ((FileConfiguration)this.config).save(this.plugin.getPropertyFile(this.file));
         } catch (Error | Exception e) {
            e.printStackTrace();
         }
      }
   }

   public T getDefaultValue() {
      return this.defaultValue;
   }
}
