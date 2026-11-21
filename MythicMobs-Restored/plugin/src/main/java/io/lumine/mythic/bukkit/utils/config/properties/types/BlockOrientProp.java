package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.BlockLocus;
import io.lumine.mythic.bukkit.utils.serialize.BlockOrient;
import io.lumine.mythic.bukkit.utils.serialize.Direction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class BlockOrientProp extends PropertyType<BlockOrient> {
   private final BlockOrient defaultValue;

   public BlockOrientProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.defaultValue = BlockOrient.of(BlockLocus.of(0, 0, 0), Direction.of(0.0F, 0.0F));
   }

   public BlockOrientProp(LuminePlugin plugin, Object file, String node, BlockOrient defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected BlockOrient compute(ConfigurationSection section, String node) {
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

               return BlockOrient.of(BlockLocus.of(x, y, z), Direction.of(yaw, pitch));
            } catch (Error | Exception e) {
               e.printStackTrace();
               return null;
            }
         }
      }
   }

   public void set(String holder, BlockOrient value) {
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

   public BlockOrient getDefaultValue() {
      return this.defaultValue;
   }
}
