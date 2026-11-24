package net.Indyuce.mmocore.api.util.math;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class VectorRotation {
   private final float yaw;
   private final float pitch;

   public VectorRotation(Location source) {
      this.yaw = source.getYaw();
      this.pitch = source.getPitch();
   }

   public Vector rotate(Vector vec) {
      vec = this.rotateX(vec, this.pitch / 180.0F * Math.PI);
      return this.rotateY(vec, -this.yaw / 180.0F * Math.PI);
   }

   private Vector rotateX(Vector vec, double a) {
      double y = vec.getY() * Math.cos(a) - vec.getZ() * Math.sin(a);
      double z = vec.getY() * Math.sin(a) + vec.getZ() * Math.cos(a);
      return vec.setY(y).setZ(z);
   }

   private Vector rotateY(Vector vec, double b) {
      double x = vec.getX() * Math.cos(b) + vec.getZ() * Math.sin(b);
      double z = vec.getX() * -Math.sin(b) + vec.getZ() * Math.cos(b);
      return vec.setX(x).setZ(z);
   }
}
