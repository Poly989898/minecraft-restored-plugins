package io.lumine.mythic.bukkit.utils.numbers;

import org.bukkit.util.Vector;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public final class Maths {
   public static final float PI = (float) Math.PI;
   public static final double TAU = Math.PI * 2;
   public static final double PI_2 = Math.PI / 2;
   public static final double PI_4 = Math.PI / 4;
   public static final double EPSILON = 1.0E-5;
   public static final float DEG2RAD = (float) (Math.PI / 180.0);
   public static final float RAD2DEG = (float) (180.0 / Math.PI);
   protected static double movementResolution = 0.001;
   private static final int SIN_BITS = 12;
   private static final int SIN_MASK = ~(-1 << SIN_BITS);
   private static final int SIN_COUNT = SIN_MASK + 1;
   private static final float radFull = (float) (Math.PI * 2);
   private static final float radToIndex = SIN_COUNT / radFull;
   private static final float degFull = 360.0F;
   private static final float degToIndex = SIN_COUNT / degFull;
   private static final float DISTANCE = 0.5F;
   private static final float[] sin = new float[SIN_COUNT];
   private static final float[] cos = new float[SIN_COUNT];

   public static float sin(float rad) {
      return sin[(int)(rad * radToIndex) & SIN_MASK];
   }

   public static float cos(float rad) {
      return cos[(int)(rad * radToIndex) & SIN_MASK];
   }

   public static float toRadians(double degree) {
      return (float)org.joml.Math.toRadians(degree);
   }

   public static boolean isSimilar(float a, float b) {
      return org.joml.Math.abs(b - a) < 1.0E-5;
   }

   public static boolean isSimilar(Vector a, Vector b) {
      return org.joml.Math.abs(b.getX() - a.getX()) < movementResolution
         && org.joml.Math.abs(b.getY() - a.getY()) < movementResolution
         && org.joml.Math.abs(b.getZ() - a.getZ()) < movementResolution;
   }

   public static Vector3f getEulerAnglesZYX(Quaternionf quaternionf, Vector3f eulerAngles) {
      float w = quaternionf.w;
      float x = quaternionf.x;
      float y = quaternionf.y;
      float z = quaternionf.z;
      eulerAngles.x = org.joml.Math.atan2(2.0F * (y * z + x * w), z * z - y * y - x * x + w * w);
      eulerAngles.y = org.joml.Math.safeAsin(-2.0F * (x * z - w * y));
      eulerAngles.z = org.joml.Math.atan2(x * y + w * z, 0.5F - y * y - z * z);
      return eulerAngles;
   }

   public static Vector3d getEulerAnglesZYX(Quaterniond quaterniond, Vector3d eulerAngles) {
      double w = quaterniond.w;
      double x = quaterniond.x;
      double y = quaterniond.y;
      double z = quaterniond.z;
      eulerAngles.x = org.joml.Math.atan2(2.0 * (y * z + x * w), z * z - y * y - x * x + w * w);
      eulerAngles.y = org.joml.Math.safeAsin(-2.0 * (x * z - w * y));
      eulerAngles.z = org.joml.Math.atan2(x * y + w * z, 0.5 - y * y - z * z);
      return eulerAngles;
   }

   private Maths() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }

   static {
      for (int i = 0; i < SIN_COUNT; i++) {
         sin[i] = org.joml.Math.sin((i + 0.5F) / SIN_COUNT * radFull);
         cos[i] = org.joml.Math.cos((i + 0.5F) / SIN_COUNT * radFull);
      }

      for (int i = 0; i < 360; i += 90) {
         sin[(int)(i * degToIndex) & SIN_MASK] = (float)org.joml.Math.sin(i * Math.PI / 180.0);
         cos[(int)(i * degToIndex) & SIN_MASK] = (float)org.joml.Math.cos(i * Math.PI / 180.0);
      }
   }
}
