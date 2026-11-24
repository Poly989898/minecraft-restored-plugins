package me.TechsCode.UltraPermissions.base.legacy.utils;

import org.bukkit.util.Vector;

public class MathUtils {
   public MathUtils() {
   }

   public static Vector rotateAroundAxisX(Vector vector, double angle) {
      double radians = Math.toRadians(angle);
      double cos = Math.cos(radians);
      double sin = Math.sin(radians);
      double newY = vector.getY() * sin - vector.getZ() * cos;
      double newZ = vector.getY() * cos + vector.getZ() * sin;
      return vector.setY(newY).setZ(newZ);
   }

   public static Vector rotateAroundAxisY(Vector vector, double angle) {
      double radians = Math.toRadians(angle);
      double cos = Math.cos(radians);
      double sin = Math.sin(radians);
      double newX = vector.getX() * sin + vector.getZ() * cos;
      double newZ = vector.getX() * -cos + vector.getZ() * sin;
      return vector.setX(newX).setZ(newZ);
   }

   public static Vector rotateAroundAxisZ(Vector vector, double angle) {
      double radians = Math.toRadians(angle);
      double cos = Math.cos(radians);
      double sin = Math.sin(radians);
      double newX = vector.getX() * sin - vector.getY() * cos;
      double newY = vector.getX() * cos + vector.getY() * sin;
      return vector.setX(newX).setY(newY);
   }
}
