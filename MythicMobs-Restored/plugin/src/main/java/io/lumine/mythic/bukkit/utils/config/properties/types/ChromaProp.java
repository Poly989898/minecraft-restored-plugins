package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import org.bukkit.configuration.ConfigurationSection;

public class ChromaProp extends PropertyType<Chroma> {
   private final Chroma defaultValue;

   public ChromaProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = Chroma.of(255, 255, 255, 255);
   }

   public ChromaProp(LuminePlugin plugin, Object file, String node, Chroma defaultValue) {
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
            String value = section.getString(node);
            return value == null ? null : Chroma.of(value);
         }
      }
   }

   public Chroma getDefaultValue() {
      return this.defaultValue;
   }
}
