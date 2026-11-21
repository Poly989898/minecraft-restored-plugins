package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.BlockPosition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class BlockPositionProp extends PropertyType<BlockPosition> {
   private final BlockPosition defaultValue;

   public BlockPositionProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = BlockPosition.of(0, 0, 0, "world");
   }

   public BlockPositionProp(LuminePlugin plugin, Object file, String node, BlockPosition defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected BlockPosition compute(ConfigurationSection section, String node) {
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
               int y = Integer.valueOf(split[1]);
               int z = Integer.valueOf(split[2]);
               String world = split[3];
               return BlockPosition.of(x, y, z, world);
            } catch (Error | Exception e) {
               e.printStackTrace();
               return null;
            }
         }
      }
   }

   public void set(String holder, BlockPosition value) {
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

   public BlockPosition getDefaultValue() {
      return this.defaultValue;
   }
}
