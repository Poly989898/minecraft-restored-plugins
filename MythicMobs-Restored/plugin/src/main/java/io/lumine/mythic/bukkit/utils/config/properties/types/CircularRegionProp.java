package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.CircularRegion;
import org.bukkit.configuration.ConfigurationSection;

public class CircularRegionProp extends PropertyType<CircularRegion> {
   private final PositionProp center;
   private final DoubleProp radius;

   public CircularRegionProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.center = new PositionProp(plugin, file, node + ".Center");
      this.radius = new DoubleProp(plugin, file, node + ".Radius");
   }

   public CircularRegionProp(LuminePlugin plugin, Object file, String node, CircularRegion defaultValue) {
      super(plugin, file, node);
      this.center = new PositionProp(plugin, file, node + ".Center", defaultValue.getCenter());
      this.radius = new DoubleProp(plugin, file, node + ".Radius", defaultValue.getRadius());
   }

   protected CircularRegion compute(ConfigurationSection section, String node) {
      return CircularRegion.of(this.center.get(section), this.radius.get(section));
   }

   protected CircularRegion getDefaultValue() {
      return CircularRegion.of(this.center.getDefaultValue(), this.radius.getDefaultValue());
   }

   public void set(String holder, CircularRegion value) {
      if (this.config != null) {
         this.center.set(holder, value.getCenter());
         this.radius.set(holder, value.getRadius());
      }
   }
}
