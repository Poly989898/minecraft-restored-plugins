package io.lumine.mythic.core.skills.pins;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.PointListProp;
import io.lumine.mythic.bukkit.utils.serialize.Point;
import io.lumine.mythic.core.config.Scope;
import java.io.File;
import java.util.List;

public class MultiPin extends Pin {
   private static final PointListProp LOCATIONS = Property.PointList(Scope.CONFIG, "Locations");

   public MultiPin(Pack pack, File file, String node) {
      super(pack, file, node);
   }

   public MultiPin(Pack pack, File file, String node, AbstractLocation location) {
      super(pack, file, node, location);
   }

   @Override
   public void loadLocations() {
      for (Point point : LOCATIONS.fget(this.file, this)) {
         this.locations.add(BukkitAdapter.adapt(point.toLocation()));
      }
   }

   @Override
   public void save() {
      List<Point> points = Lists.newArrayList();

      for (AbstractLocation location : this.locations) {
         points.add(Point.of(BukkitAdapter.adapt(location)));
      }

      LOCATIONS.fset(this.file, this, points);
   }
}
