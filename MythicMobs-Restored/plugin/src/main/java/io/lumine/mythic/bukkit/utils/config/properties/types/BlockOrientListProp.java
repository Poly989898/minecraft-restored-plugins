package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.BlockLocus;
import io.lumine.mythic.bukkit.utils.serialize.BlockOrient;
import io.lumine.mythic.bukkit.utils.serialize.Direction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;

public class BlockOrientListProp extends PropertyType<List<BlockOrient>> {
   public BlockOrientListProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected List<BlockOrient> compute(ConfigurationSection section, String node) {
      List<BlockOrient> result = new ArrayList<>();
      if (section == null) {
         return result;
      } else {
         List<String> listprop = section.getStringList(node);
         if (listprop == null) {
            return result;
         } else {
            for (String prop : listprop) {
               try {
                  String[] split = prop.split(",");
                  int x = Integer.valueOf(split[0]);
                  int y = Integer.valueOf(split[1]);
                  int z = Integer.valueOf(split[2]);
                  float yaw = split.length > 3 ? Float.valueOf(split[3]) : 0.0F;
                  float pitch = split.length > 4 ? Float.valueOf(split[4]) : 0.0F;
                  result.add(BlockOrient.of(BlockLocus.of(x, y, z), Direction.of(yaw, pitch)));
               } catch (Error | Exception e) {
                  e.printStackTrace();
               }
            }

            return result;
         }
      }
   }

   protected List<BlockOrient> getDefaultValue() {
      return Collections.emptyList();
   }

   public void set(String holder, List<BlockOrient> value) {
      if (this.config != null) {
         List<String> out = new ArrayList<>();

         for (BlockOrient orient : value) {
            String o = orient.getLocus().getX()
               + ","
               + orient.getLocus().getY()
               + ","
               + orient.getLocus().getZ()
               + ","
               + orient.getDirection().getYaw()
               + ","
               + orient.getDirection().getPitch();
            out.add(o);
         }

         this.config.set(this.node, out);
      }
   }
}
