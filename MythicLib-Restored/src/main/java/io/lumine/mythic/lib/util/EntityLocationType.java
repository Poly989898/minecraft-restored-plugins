package io.lumine.mythic.lib.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public enum EntityLocationType {
   FEET(0.0),
   BODY(0.5),
   TOP(1.0),
   EYES(0.888888888888889);

   private final double heightPercentage;

   EntityLocationType(double heightPercentage) {
      this.heightPercentage = heightPercentage;
   }

   public Location getLocation(Entity entity) {
      Location loc = entity.getLocation();
      loc.add(0.0, entity.getHeight() * this.heightPercentage, 0.0);
      return loc;
   }
}
