package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import io.lumine.mythic.bukkit.utils.serialize.Zone;
import org.bukkit.configuration.ConfigurationSection;

public class ZoneProp extends PropertyType<Zone> {
   private final LocProp min;
   private final LocProp max;

   public ZoneProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.min = new LocProp(plugin, file, node + ".Min", Locus.of(0.0, 0.0, 0.0));
      this.max = new LocProp(plugin, file, node + ".Max", Locus.of(0.0, 0.0, 0.0));
   }

   public ZoneProp(LuminePlugin plugin, Object file, String node, Zone defaultValue) {
      super(plugin, file, node);
      this.min = new LocProp(plugin, file, node + ".Min", defaultValue.getMin());
      this.max = new LocProp(plugin, file, node + ".Max", defaultValue.getMax());
   }

   protected Zone compute(ConfigurationSection section, String node) {
      return Zone.of(this.min.get(section), this.max.get(section));
   }

   protected Zone getDefaultValue() {
      return Zone.of(this.min.getDefaultValue(), this.max.getDefaultValue());
   }

   public void set(String holder, Zone value) {
      if (this.config != null) {
         this.min.set(holder, value.getMin());
         this.max.set(holder, value.getMax());
      }
   }
}
