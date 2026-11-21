package io.lumine.mythic.core.skills.pins;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.PointProp;
import io.lumine.mythic.bukkit.utils.serialize.Point;
import io.lumine.mythic.core.config.Scope;
import java.io.File;

public class SinglePin extends Pin {
   private static final PointProp LOCATIONS = Property.Point(Scope.CONFIG, "Location");

   public SinglePin(Pack pack, File file, String node) {
      super(pack, file, node);
   }

   public SinglePin(Pack pack, File file, String node, AbstractLocation location) {
      super(pack, file, node, location);
   }

   @Override
   public void loadLocations() {
      Point point = LOCATIONS.fget(this.file, this);
      this.locations.add(BukkitAdapter.adapt(point.toLocation()));
   }

   @Override
   public void save() {
      Point point = null;

      for (AbstractLocation location : this.locations) {
         point = Point.of(BukkitAdapter.adapt(location)).round();
      }

      LOCATIONS.fset(this.file, this, point);
   }
}
