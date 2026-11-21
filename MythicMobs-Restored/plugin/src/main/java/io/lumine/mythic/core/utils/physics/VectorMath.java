package io.lumine.mythic.core.utils.physics;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class VectorMath {
   protected static final double DEGREES_TO_RADIANS = Math.PI / 180.0;

   @NotNull
   public static Vector fromYawAndPitch(float yaw, float pitch) {
      yaw += 90.0F;
      double y = Math.sin(pitch * (Math.PI / 180.0));
      double div = Math.cos(pitch * (Math.PI / 180.0));
      double x = Math.cos(yaw * (Math.PI / 180.0)) * div;
      double z = Math.sin(yaw * (Math.PI / 180.0)) * div;
      return new Vector(x, y, z);
   }

   @NotNull
   public static Vector angleBetween(@NotNull Location a, @NotNull Location b) {
      return new Vector(b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ());
   }

   @NotNull
   public static Vector angleBetween(@NotNull Vector a, @NotNull Vector b) {
      return new Vector(b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ());
   }
}
