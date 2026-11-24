package io.lumine.mythic.lib.util;

import io.lumine.mythic.lib.UtilityMethods;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@Deprecated
public class SkillOrientation {
   private final Vector pivot;
   private final Vector axis;

   public SkillOrientation(Location pivot, Vector axis) {
      this.pivot = pivot.toVector();
      this.axis = axis;
      Validate.isTrue(axis.lengthSquared() > 0.0, "Axis cannot be zero");
   }

   public Location getRotated(Location loc) {
      return UtilityMethods.rotate(loc.toVector().subtract(this.pivot), this.axis).toLocation(loc.getWorld());
   }
}
