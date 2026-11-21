package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.ChunkPosition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ChunkPositionProp extends PropertyType<ChunkPosition> {
   private final ChunkPosition defaultValue;

   public ChunkPositionProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = ChunkPosition.of(0, 0, "world");
   }

   public ChunkPositionProp(LuminePlugin plugin, Object file, String node, ChunkPosition defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected ChunkPosition compute(ConfigurationSection section, String node) {
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
               String world = split[2];
               return ChunkPosition.of(x, z, world);
            } catch (Error | Exception e) {
               e.printStackTrace();
               return null;
            }
         }
      }
   }

   public void set(String holder, ChunkPosition value) {
      if (this.config != null) {
         String val = value.getX() + "," + value.getZ() + "," + value.getWorld();
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

   public ChunkPosition getDefaultValue() {
      return this.defaultValue;
   }
}
