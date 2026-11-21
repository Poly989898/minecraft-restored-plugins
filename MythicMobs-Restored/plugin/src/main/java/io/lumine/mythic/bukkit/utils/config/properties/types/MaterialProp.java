package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import org.bukkit.Material;

public class MaterialProp extends EnumProp<Material> {
   public MaterialProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, Material.class, node);
   }

   public MaterialProp(LuminePlugin plugin, Object file, String node, Material defaultValue) {
      super(plugin, file, Material.class, node, defaultValue);
   }
}
