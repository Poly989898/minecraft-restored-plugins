package io.lumine.mythic.bukkit.utils.numbers;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Vectors {
   public static float clampYaw(float yaw) {
      while (yaw < -180.0F) {
         yaw += 360.0F;
      }

      while (yaw >= 180.0F) {
         yaw -= 360.0F;
      }

      return yaw;
   }

   public static final Vector rotateAroundAxisX(Vector v, double angle) {
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double y = v.getY() * cos - v.getZ() * sin;
      double z = v.getY() * sin + v.getZ() * cos;
      return v.setY(y).setZ(z);
   }

   public static final Vector rotateAroundAxisY(Vector v, double angle) {
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double x = v.getX() * cos + v.getZ() * sin;
      double z = v.getX() * -sin + v.getZ() * cos;
      return v.setX(x).setZ(z);
   }

   public static final Vector rotateAroundAxisZ(Vector v, double angle) {
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double x = v.getX() * cos - v.getY() * sin;
      double y = v.getX() * sin + v.getY() * cos;
      return v.setX(x).setY(y);
   }

   public static final Vector rotateVector(Vector v, double angleX, double angleY, double angleZ) {
      rotateAroundAxisX(v, angleX);
      rotateAroundAxisY(v, angleY);
      rotateAroundAxisZ(v, angleZ);
      return v;
   }

   public static final Vector rotateVector(Vector v, Location location) {
      return rotateVector(v, location.getYaw(), location.getPitch());
   }

   public static final Vector rotateVector(Vector v, float yawDegrees, float pitchDegrees) {
      double yaw = Math.toRadians(-1.0F * (yawDegrees + 90.0F));
      double pitch = Math.toRadians(-pitchDegrees);
      double cosYaw = Math.cos(yaw);
      double cosPitch = Math.cos(pitch);
      double sinYaw = Math.sin(yaw);
      double sinPitch = Math.sin(pitch);
      double initialX = v.getX();
      double initialY = v.getY();
      double x = initialX * cosPitch - initialY * sinPitch;
      double y = initialX * sinPitch + initialY * cosPitch;
      double initialZ = v.getZ();
      double z = initialZ * cosYaw - x * sinYaw;
      x = initialZ * sinYaw + x * cosYaw;
      return new Vector(x, y, z);
   }

   public static final double angleToXAxis(Vector vector) {
      return Math.atan2(vector.getX(), vector.getY());
   }

   private Vectors() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
