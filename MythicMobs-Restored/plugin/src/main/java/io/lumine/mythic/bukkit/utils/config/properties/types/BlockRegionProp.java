package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.BlockRegion;
import org.bukkit.configuration.ConfigurationSection;

public class BlockRegionProp extends PropertyType<BlockRegion> {
   private final BlockPositionProp min;
   private final BlockPositionProp max;

   public BlockRegionProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.min = new BlockPositionProp(plugin, file, node + ".Min");
      this.max = new BlockPositionProp(plugin, file, node + ".Max");
   }

   public BlockRegionProp(LuminePlugin plugin, Object file, String node, BlockRegion defaultValue) {
      super(plugin, file, node);
      this.min = new BlockPositionProp(plugin, file, node + ".Min", defaultValue.getMin());
      this.max = new BlockPositionProp(plugin, file, node + ".Max", defaultValue.getMax());
   }

   protected BlockRegion compute(ConfigurationSection section, String node) {
      return BlockRegion.of(this.min.get(section), this.max.get(section));
   }

   protected BlockRegion getDefaultValue() {
      return BlockRegion.of(this.min.getDefaultValue(), this.max.getDefaultValue());
   }

   public void set(String holder, BlockRegion value) {
      if (this.config != null) {
         this.min.set(holder, value.getMin());
         this.max.set(holder, value.getMax());
      }
   }
}
