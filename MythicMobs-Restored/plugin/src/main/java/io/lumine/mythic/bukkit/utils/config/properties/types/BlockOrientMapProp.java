package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.BlockLocus;
import io.lumine.mythic.bukkit.utils.serialize.BlockOrient;
import io.lumine.mythic.bukkit.utils.serialize.Direction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.configuration.ConfigurationSection;

public class BlockOrientMapProp extends PropertyType<Map<String, BlockOrient>> {
   public BlockOrientMapProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
   }

   protected Map<String, BlockOrient> compute(ConfigurationSection section, String node) {
      Map<String, BlockOrient> result = new HashMap<>();
      if (section == null) {
         return result;
      } else {
         section = section.getConfigurationSection(node);
         if (section == null) {
            return result;
         } else {
            for (String key : section.getKeys(false)) {
               String prop = section.getString(key);

               try {
                  String[] split = prop.split(",");
                  int x = Integer.valueOf(split[0]);
                  int y = Integer.valueOf(split[1]);
                  int z = Integer.valueOf(split[2]);
                  float yaw = Float.valueOf(split[3]);
                  float pitch = Float.valueOf(split[4]);
                  result.put(key, BlockOrient.of(BlockLocus.of(x, y, z), Direction.of(yaw, pitch)));
               } catch (Error | Exception e) {
                  e.printStackTrace();
               }
            }

            return result;
         }
      }
   }

   protected Map<String, BlockOrient> getDefaultValue() {
      return Collections.emptyMap();
   }

   public void set(String holder, Map<String, BlockOrient> value) {
      if (this.config != null) {
         List<String> out = new ArrayList<>();

         for (Entry<String, BlockOrient> entry : value.entrySet()) {
            BlockOrient orient = entry.getValue();
            String o = entry.getKey()
               + " "
               + orient.getLocus().getX()
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
