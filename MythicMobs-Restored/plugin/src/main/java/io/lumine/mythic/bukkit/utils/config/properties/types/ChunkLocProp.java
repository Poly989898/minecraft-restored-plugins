package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.ChunkLocus;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ChunkLocProp extends PropertyType<ChunkLocus> {
   private final ChunkLocus defaultValue;

   public ChunkLocProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = ChunkLocus.of(0, 0);
   }

   public ChunkLocProp(LuminePlugin plugin, Object file, String node, ChunkLocus defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected ChunkLocus compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else {
         String prop = section.getString(node);
         if (prop == null) {
            return this.defaultValue;
         } else {
            try {
               String[] split = prop.split(",");
               int x = Integer.valueOf(split[0]);
               int z = Integer.valueOf(split[1]);
               return ChunkLocus.of(x, z);
            } catch (Error | Exception e) {
               e.printStackTrace();
               return null;
            }
         }
      }
   }

   public void set(String holder, ChunkLocus value) {
      if (this.config != null) {
         String val = value.getX() + "," + value.getZ();
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

   public ChunkLocus getDefaultValue() {
      return this.defaultValue;
   }
}
