package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.CircularZone;
import org.bukkit.configuration.ConfigurationSection;

public class CircularZoneProp extends PropertyType<CircularZone> {
   private final LocProp center;
   private final DoubleProp radius;

   public CircularZoneProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.center = new LocProp(plugin, file, node + ".Center");
      this.radius = new DoubleProp(plugin, file, node + ".Radius");
   }

   public CircularZoneProp(LuminePlugin plugin, Object file, String node, CircularZone defaultValue) {
      super(plugin, file, node);
      this.center = new LocProp(plugin, file, node + ".Center", defaultValue.getCenter());
      this.radius = new DoubleProp(plugin, file, node + ".Radius", defaultValue.getRadius());
   }

   protected CircularZone compute(ConfigurationSection section, String node) {
      return CircularZone.of(this.center.get(section), this.radius.get(section));
   }

   protected CircularZone getDefaultValue() {
      return CircularZone.of(this.center.getDefaultValue(), this.radius.getDefaultValue());
   }

   public void set(String holder, CircularZone value) {
      if (this.config != null) {
         this.center.set(holder, value.getCenter());
         this.radius.set(holder, value.getRadius());
      }
   }
}
