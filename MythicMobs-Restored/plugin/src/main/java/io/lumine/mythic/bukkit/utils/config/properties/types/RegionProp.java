package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Region;
import org.bukkit.configuration.ConfigurationSection;

public class RegionProp extends PropertyType<Region> {
   private final PositionProp min;
   private final PositionProp max;

   public RegionProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.min = new PositionProp(plugin, file, node + ".Min");
      this.max = new PositionProp(plugin, file, node + ".Max");
   }

   public RegionProp(LuminePlugin plugin, Object file, String node, Region defaultValue) {
      super(plugin, file, node);
      this.min = new PositionProp(plugin, file, node + ".Min", defaultValue.getMin());
      this.max = new PositionProp(plugin, file, node + ".Max", defaultValue.getMax());
   }

   protected Region compute(ConfigurationSection section, String node) {
      return Region.of(this.min.get(section), this.max.get(section));
   }

   protected Region getDefaultValue() {
      return Region.of(this.min.getDefaultValue(), this.max.getDefaultValue());
   }

   @Override
   public void setScope(Object scope) {
      super.setScope(scope);
      if (this.min != null) {
         this.min.setScope(scope);
      }

      if (this.max != null) {
         this.max.setScope(scope);
      }
   }

   public void set(String holder, Region value) {
      if (this.config != null) {
         this.min.set(holder, value.getMin());
         this.max.set(holder, value.getMax());
      }
   }
}
