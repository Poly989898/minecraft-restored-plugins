package com.ticxo.modelengine.api.utils.math;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import java.util.UUID;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class TMath {
   public static final float PI = (float) Math.PI;
   public static final double TAU = Math.PI * 2;
   public static final double PI_2 = Math.PI / 2;
   public static final double PI_4 = Math.PI / 4;
   public static final float EPSILON = 1.0E-5F;
   public static final float JOML_EPSILON = 0.001F;
   public static final double D_EPSILON = 1.0E-5;
   public static final float DEG2RAD = (float) (Math.PI / 180.0);
   public static final float RAD2DEG = (float) (180.0 / Math.PI);
   public static final double D_DEG2RAD = Math.PI / 180.0;
   public static final double D_RAD2DEG = 180.0 / Math.PI;
   protected static float LOWER_SCALE_LIMIT = 0.0F;
   protected static double MOVEMENT_RESOLUTION = 0.001;

   public static boolean isSimilar(float a, float b) {
      return org.joml.Math.abs(b - a) < 1.0E-5F;
   }

   public static boolean isSimilar(double a, double b) {
      return org.joml.Math.abs(b - a) < 1.0E-5F;
   }

   public static boolean isSimilar(float a, float b, float epsilon) {
      return org.joml.Math.abs(b - a) < epsilon;
   }

   public static boolean isSimilar(Vector3f a, Vector3f b) {
      if (a == null && b == null) {
         return true;
      } else {
         return a != null && b != null ? isSimilar(a.x, b.x, 0.001F) && isSimilar(a.y, b.y, 0.001F) && isSimilar(a.z, b.z, 0.001F) : false;
      }
   }

   public static boolean isSimilar(Quaternionf a, Quaternionf b) {
      if (a == null && b == null) {
         return true;
      } else {
         return a != null && b != null
            ? isSimilar(a.x, b.x, 0.001F) && isSimilar(a.y, b.y, 0.001F) && isSimilar(a.z, b.z, 0.001F) && isSimilar(a.w, b.w, 0.001F)
            : false;
      }
   }

   public static double clamp(double value, double min, double max) {
      return org.joml.Math.min(org.joml.Math.max(value, min), max);
   }

   public static float clamp(float value, float min, float max) {
      return org.joml.Math.min(org.joml.Math.max(value, min), max);
   }

   public static int clamp(int value, int min, int max) {
      return org.joml.Math.min(org.joml.Math.max(value, min), max);
   }

   public static int absMax(float a, float b, float c) {
      float absA = org.joml.Math.abs(a);
      float absB = org.joml.Math.abs(b);
      float absC = org.joml.Math.abs(c);
      if (absA > absB) {
         return absA > absC ? 0 : 2;
      } else {
         return absB > absC ? 1 : 2;
      }
   }

   public static byte setBit(byte dataItem, int bitOffset, boolean flag) {
      byte f = (byte)(1 << bitOffset);
      return (byte)(flag ? dataItem | f : dataItem & ~f);
   }

   public static boolean getBit(byte dataItem, int bitOffset) {
      return (dataItem & 1 << bitOffset) != 0;
   }

   public static int floor(double val) {
      return (int)org.joml.Math.floor(val);
   }

   public static int ceil(double val) {
      return (int)org.joml.Math.ceil(val);
   }

   public static int tryParse(String val, int def) {
      if (val == null) {
         return def;
      }

      try {
         return Integer.parseInt(val);
      } catch (NumberFormatException ignored) {
         return def;
      }
   }

   public static double tryParse(String val, double def) {
      if (val == null) {
         return def;
      }

      try {
         return Double.parseDouble(val);
      } catch (NumberFormatException ignored) {
         return def;
      }
   }

   public static boolean isBoundingBoxWithinDistance(@NotNull Vector start, @NotNull Vector direction, BoundingBox boundingBox, double maxDistance) {
      double startX = start.getX();
      double startY = start.getY();
      double startZ = start.getZ();
      Vector dir = direction.clone();
      if (dir.getX() == 0.0) {
         dir.setX(0);
      }

      if (dir.getY() == 0.0) {
         dir.setY(0);
      }

      if (dir.getZ() == 0.0) {
         dir.setZ(0);
      }

      dir.normalize();
      double dirX = dir.getX();
      double dirY = dir.getY();
      double dirZ = dir.getZ();
      double divX = 1.0 / dirX;
      double divY = 1.0 / dirY;
      double divZ = 1.0 / dirZ;
      double tMin;
      double tMax;
      if (dirX >= 0.0) {
         tMin = (boundingBox.getMinX() - startX) * divX;
         tMax = (boundingBox.getMaxX() - startX) * divX;
      } else {
         tMin = (boundingBox.getMaxX() - startX) * divX;
         tMax = (boundingBox.getMinX() - startX) * divX;
      }

      double tyMin;
      double tyMax;
      if (dirY >= 0.0) {
         tyMin = (boundingBox.getMinY() - startY) * divY;
         tyMax = (boundingBox.getMaxY() - startY) * divY;
      } else {
         tyMin = (boundingBox.getMaxY() - startY) * divY;
         tyMax = (boundingBox.getMinY() - startY) * divY;
      }

      if (!(tMin > tyMax) && !(tMax < tyMin)) {
         if (tyMin > tMin) {
            tMin = tyMin;
         }

         if (tyMax < tMax) {
            tMax = tyMax;
         }

         double tzMin;
         double tzMax;
         if (dirZ >= 0.0) {
            tzMin = (boundingBox.getMinZ() - startZ) * divZ;
            tzMax = (boundingBox.getMaxZ() - startZ) * divZ;
         } else {
            tzMin = (boundingBox.getMaxZ() - startZ) * divZ;
            tzMax = (boundingBox.getMinZ() - startZ) * divZ;
         }

         if (!(tMin > tzMax) && !(tMax < tzMin)) {
            if (tzMin > tMin) {
               tMin = tzMin;
            }

            if (tzMax < tMax) {
               tMax = tzMax;
            }

            return tMax < 0.0 ? false : !(tMin > maxDistance);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static double distanceSquaredToBoundingBox(Vector origin, BoundingBox box) {
      double x = org.joml.Math.max(org.joml.Math.abs(origin.getX() - box.getCenterX()) - box.getWidthX() * 0.5, 0.0);
      double y = org.joml.Math.max(org.joml.Math.abs(origin.getY() - box.getCenterY()) - box.getHeight() * 0.5, 0.0);
      double z = org.joml.Math.max(org.joml.Math.abs(origin.getZ() - box.getCenterZ()) - box.getWidthZ() * 0.5, 0.0);
      return x * x + y * y + z * z;
   }

   public static boolean isSimilar(Vector a, Vector b) {
      return org.joml.Math.abs(b.getX() - a.getX()) < MOVEMENT_RESOLUTION
         && org.joml.Math.abs(b.getY() - a.getY()) < MOVEMENT_RESOLUTION
         && org.joml.Math.abs(b.getZ() - a.getZ()) < MOVEMENT_RESOLUTION;
   }

   public static EulerAngle makeAngle(double x, double y, double z) {
      return new EulerAngle(org.joml.Math.toRadians(x), org.joml.Math.toRadians(y), org.joml.Math.toRadians(z));
   }

   public static EulerAngle add(EulerAngle a, EulerAngle b) {
      return a.add(b.getX(), b.getY(), b.getZ());
   }

   public static float wrapRadian(float r) {
      r = (float)(r % (Math.PI * 2));
      if (r < -Math.PI) {
         r = (float)(r + (Math.PI * 2));
      }

      if (r > Math.PI) {
         r = (float)(r - (Math.PI * 2));
      }

      return r;
   }

   public static float wrapDegree(float r) {
      if (r >= -180.0F && r <= 180.0F) {
         return r;
      }

      r %= 360.0F;
      if (r < -180.0F) {
         r += 360.0F;
      }

      if (r > 180.0F) {
         r -= 360.0F;
      }

      return r;
   }

   public static float radianDifference(float a, float b) {
      return wrapRadian(b - a);
   }

   public static float degreeDifference(float a, float b) {
      return wrapDegree(b - a);
   }

   public static float rotateIfNecessary(float current, float target, float negativeClamp, float positiveClamp) {
      float delta = degreeDifference(current, target);
      float clampedDelta = clamp(delta, negativeClamp, positiveClamp);
      return target - clampedDelta;
   }

   public static byte rotToByte(float rot) {
      return (byte)(rot * 256.0F / 360.0F);
   }

   public static float byteToRot(byte rot) {
      return rot / 256.0F * 360.0F;
   }

   public static double lerp(double a, double b, double t) {
      return (1.0 - t) * a + t * b;
   }

   public static double invLerp(double a, double b, double l) {
      return (l - a) / (b - a);
   }

   public static float lerp(float a, float b, float t) {
      return (1.0F - t) * a + t * b;
   }

   public static float invLerp(float a, float b, float l) {
      return (l - a) / (b - a);
   }

   public static double lerp(double a, double b, double aT, double bT) {
      return aT * a + bT * b;
   }

   public static float lerp(float a, float b, float aT, float bT) {
      return aT * a + bT * b;
   }

   public static double rotLerp(double a, double b, double t) {
      return a + degreeDifference((float)a, (float)b) * t;
   }

   public static float rotLerp(float a, float b, double t) {
      return (float)(a + degreeDifference(a, b) * t);
   }

   public static double smoothLerp(double a, double b, double c, double d, double t) {
      double t0 = 0.0;
      double t1 = 1.0;
      double t2 = 2.0;
      double t3 = 3.0;
      t = (t2 - t1) * t + t1;
      double a1 = lerp(a, b, (t1 - t) / (t1 - t0), (t - t0) / (t1 - t0));
      double a2 = lerp(b, c, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
      double a3 = lerp(c, d, (t3 - t) / (t3 - t2), (t - t2) / (t3 - t2));
      double b1 = lerp(a1, a2, (t2 - t) / (t2 - t0), (t - t0) / (t2 - t0));
      double b2 = lerp(a2, a3, (t3 - t) / (t3 - t1), (t - t1) / (t3 - t1));
      return lerp(b1, b2, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
   }

   public static Vector lerp(Vector a, Vector b, double t) {
      return new Vector(lerp(a.getX(), b.getX(), t), lerp(a.getY(), b.getY(), t), lerp(a.getZ(), b.getZ(), t));
   }

   public static Vector lerp(Vector a, Vector b, double aT, double bT) {
      return new Vector(lerp(a.getX(), b.getX(), aT, bT), lerp(a.getY(), b.getY(), aT, bT), lerp(a.getZ(), b.getZ(), aT, bT));
   }

   public static Vector3f lerp(Vector3f a, Vector3f b, double t) {
      return new Vector3f((float)lerp(a.x, b.x, t), (float)lerp(a.y, b.y, t), (float)lerp(a.z, b.z, t));
   }

   public static Vector3f lerp(Vector3f a, Vector3f b, float aT, float bT) {
      return new Vector3f(lerp(a.x, b.x, aT, bT), lerp(a.y, b.y, aT, bT), lerp(a.z, b.z, aT, bT));
   }

   public static Vector smoothLerp(Vector a, Vector b, Vector c, Vector d, double t) {
      double t0 = 0.0;
      double t1 = 1.0;
      double t2 = 2.0;
      double t3 = 3.0;
      t = (t2 - t1) * t + t1;
      Vector a1 = lerp(a, b, (t1 - t) / (t1 - t0), (t - t0) / (t1 - t0));
      Vector a2 = lerp(b, c, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
      Vector a3 = lerp(c, d, (t3 - t) / (t3 - t2), (t - t2) / (t3 - t2));
      Vector b1 = lerp(a1, a2, (t2 - t) / (t2 - t0), (t - t0) / (t2 - t0));
      Vector b2 = lerp(a2, a3, (t3 - t) / (t3 - t1), (t - t1) / (t3 - t1));
      return lerp(b1, b2, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
   }

   public static Vector3f smoothLerp(Vector3f a, Vector3f b, Vector3f c, Vector3f d, float t) {
      float t0 = 0.0F;
      float t1 = 1.0F;
      float t2 = 2.0F;
      float t3 = 3.0F;
      t = (t2 - t1) * t + t1;
      Vector3f a1 = lerp(a, b, (t1 - t) / (t1 - t0), (t - t0) / (t1 - t0));
      Vector3f a2 = lerp(b, c, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
      Vector3f a3 = lerp(c, d, (t3 - t) / (t3 - t2), (t - t2) / (t3 - t2));
      Vector3f b1 = lerp(a1, a2, (t2 - t) / (t2 - t0), (t - t0) / (t2 - t0));
      Vector3f b2 = lerp(a2, a3, (t3 - t) / (t3 - t1), (t - t1) / (t3 - t1));
      return lerp(b1, b2, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
   }

   public static float cubicBezierTime(float u1, float u2, float s) {
      float inv = 1.0F - s;
      return 3.0F * u1 * s * inv * inv + 3.0F * u2 * s * s * inv + s * s * s;
   }

   public static float cubicBezierTimeDerivative(float u1, float u2, float s) {
      float inv = 1.0F - s;
      return 3.0F * u1 * inv * (1.0F - 3.0F * s) + 3.0F * u2 * s * (2.0F - 3.0F * s) + 3.0F * s * s;
   }

   public static float cubicBezierSolve(float u1, float u2, float t) {
      if (t <= 0.0F) {
         return 0.0F;
      }

      if (t >= 1.0F) {
         return 1.0F;
      }

      float s = t;

      for (int i = 0; i < 8; i++) {
         float error = cubicBezierTime(u1, u2, s) - t;
         if (error < 0.0F) {
            error = -error;
         }

         if (error < 1.0E-5F) {
            return s;
         }

         float slope = cubicBezierTimeDerivative(u1, u2, s);
         if (slope < 1.0E-6F) {
            break;
         }

         s = clamp(s - error / slope, 0.0F, 1.0F);
      }

      float lo = 0.0F;
      float hi = 1.0F;
      s = t;

      for (int i = 0; i < 20; i++) {
         if (cubicBezierTime(u1, u2, s) < t) {
            lo = s;
         } else {
            hi = s;
         }

         s = (lo + hi) * 0.5F;
      }

      return s;
   }

   public static float cubicBezierValue(float v0, float v1, float v2, float v3, float s) {
      float inv = 1.0F - s;
      float invSq = inv * inv;
      float sq = s * s;
      return invSq * inv * v0 + 3.0F * invSq * s * v1 + 3.0F * inv * sq * v2 + sq * s * v3;
   }

   public static EulerAngle lerp(EulerAngle a, EulerAngle b, double t) {
      return new EulerAngle(lerp(a.getX(), b.getX(), t), lerp(a.getY(), b.getY(), t), lerp(a.getZ(), b.getZ(), t));
   }

   public static EulerAngle lerp(EulerAngle a, EulerAngle b, double aT, double bT) {
      return new EulerAngle(lerp(a.getX(), b.getX(), aT, bT), lerp(a.getY(), b.getY(), aT, bT), lerp(a.getZ(), b.getZ(), aT, bT));
   }

   public static EulerAngle smoothLerp(EulerAngle a, EulerAngle b, EulerAngle c, EulerAngle d, double t) {
      double t0 = 0.0;
      double t1 = 1.0;
      double t2 = 2.0;
      double t3 = 3.0;
      t = (t2 - t1) * t + t1;
      EulerAngle a1 = lerp(a, b, (t1 - t) / (t1 - t0), (t - t0) / (t1 - t0));
      EulerAngle a2 = lerp(b, c, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
      EulerAngle a3 = lerp(c, d, (t3 - t) / (t3 - t2), (t - t2) / (t3 - t2));
      EulerAngle b1 = lerp(a1, a2, (t2 - t) / (t2 - t0), (t - t0) / (t2 - t0));
      EulerAngle b2 = lerp(a2, a3, (t3 - t) / (t3 - t1), (t - t1) / (t3 - t1));
      return lerp(b1, b2, (t2 - t) / (t2 - t1), (t - t1) / (t2 - t1));
   }

   public static Vector3f slerp(Vector3f a, Vector3f b, double t) {
      if (a.equals(b)) {
         return a;
      }

      Quaternionf qA = new Quaternionf().rotationZYX(a.z, a.y, a.x);
      Quaternionf qB = new Quaternionf().rotationZYX(b.z, b.y, b.x);
      qA.slerp(qB, (float)t);
      return getEulerAnglesZYX(qA, new Vector3f());
   }

   public static Vector3f getEulerAnglesZYX(Quaternionf quat, Vector3f eulerAngles) {
      return matToEulerZYX(quat.get(new Matrix3f()), eulerAngles);
   }

   public static Vector3f matToEulerZYX(Matrix3f mat, Vector3f result) {
      result.y = org.joml.Math.asin(-org.joml.Math.clamp(mat.m02, -1.0F, 1.0F));
      if (org.joml.Math.abs(mat.m02) < 0.9999999) {
         result.x = org.joml.Math.atan2(mat.m12, mat.m22);
         result.z = org.joml.Math.atan2(mat.m01, mat.m00);
      } else {
         result.x = 0.0F;
         result.z = org.joml.Math.atan2(-mat.m10, mat.m11);
      }

      return result;
   }

   public static Vector3d getEulerAnglesZYX(Quaterniond quat, Vector3d eulerAngles) {
      return matToEulerZYX(quat.get(new Matrix3d()), eulerAngles);
   }

   public static Vector3d matToEulerZYX(Matrix3d mat, Vector3d result) {
      result.y = org.joml.Math.asin(-clamp(mat.m02, -1.0, 1.0));
      if (org.joml.Math.abs(mat.m02) < 0.9999999) {
         result.x = org.joml.Math.atan2(mat.m12, mat.m22);
         result.z = org.joml.Math.atan2(mat.m01, mat.m00);
      } else {
         result.x = 0.0;
         result.z = org.joml.Math.atan2(-mat.m10, mat.m11);
      }

      return result;
   }

   public static Vector3f toEulerZYX(Quaternionf quaternion, Vector3f dest) {
      return getEulerAnglesZYX(quaternion, dest).mul((float) (180.0 / Math.PI));
   }

   public static Vector3d toEulerZYX(Quaterniond quaternion) {
      return getEulerAnglesZYX(quaternion, new Vector3d()).mul(180.0 / Math.PI);
   }

   public static Vector3d toEulerXYZ(Quaterniond quaternion) {
      return matToEulerXYZ(quaternion.get(new Matrix3d()), new Vector3d()).mul(180.0 / Math.PI);
   }

   public static Vector3d matToEulerXYZ(Matrix3d mat, Vector3d result) {
      result.y = org.joml.Math.asin(clamp(mat.m20, -1.0, 1.0));
      if (org.joml.Math.abs(mat.m20) < 0.9999999) {
         result.x = org.joml.Math.atan2(-mat.m21, mat.m22);
         result.z = org.joml.Math.atan2(-mat.m10, mat.m00);
      } else {
         result.x = org.joml.Math.atan2(mat.m12, mat.m11);
         result.z = 0.0;
      }

      return result;
   }

   public static Quaterniond fromEulerZYX(Vector3d euler) {
      return new Quaterniond().rotateZYX(euler.z * (Math.PI / 180.0), euler.y * (Math.PI / 180.0), euler.x * (Math.PI / 180.0));
   }

   public static Quaterniond fromEulerXYZ(Vector3d euler) {
      return new Quaterniond().rotateXYZ(euler.x * (Math.PI / 180.0), euler.y * (Math.PI / 180.0), euler.z * (Math.PI / 180.0));
   }

   public static boolean isInterval(double value, double interval) {
      double half = interval * 0.5;
      return isSimilar(org.joml.Math.abs(org.joml.Math.abs(value) % interval - half), half);
   }

   public static boolean isAlmostBetween(double val, double min, double max) {
      return val >= min && val <= max ? true : isSimilar(val, min) || isSimilar(val, max);
   }

   public static Vector3f toPitchYaw(Vector3f dir) {
      double hMagnitude = org.joml.Math.sqrt(dir.x * dir.x + dir.z * dir.z);
      float pitch = (float)org.joml.Math.atan2(-dir.y, hMagnitude) * (float) (180.0 / Math.PI);
      float yaw = org.joml.Math.atan2(-dir.x, dir.z) * (float) (180.0 / Math.PI);
      return new Vector3f(pitch, yaw, 0.0F);
   }

   public static Quaternionf toQuaternion(Vector3f euler) {
      return new Quaternionf().rotateZYX(euler.z, euler.y, euler.x);
   }

   public static Quaternionf toQuaternion(Vector3f euler, Quaternionf dest) {
      return dest.identity().rotateZYX(euler.z, euler.y, euler.x);
   }

   public static Vector3f fixVector(Vector3f v) {
      if (isSimilar(v.x, 0.0F)) {
         v.x = 0.0F;
      }

      if (isSimilar(v.y, 0.0F)) {
         v.y = 0.0F;
      }

      if (isSimilar(v.z, 0.0F)) {
         v.z = 0.0F;
      }

      return v;
   }

   public static Vector3d fixVector(Vector3d v) {
      if (isSimilar(v.x, 0.0)) {
         v.x = 0.0;
      }

      if (isSimilar(v.y, 0.0)) {
         v.y = 0.0;
      }

      if (isSimilar(v.z, 0.0)) {
         v.z = 0.0;
      }

      return v;
   }

   public static Vector3d fixEuler(Vector3d v) {
      fixVector(v);
      v.x = org.joml.Math.round(v.x * 10000.0) * 1.0E-4;
      v.y = org.joml.Math.round(v.y * 10000.0) * 1.0E-4;
      v.z = org.joml.Math.round(v.z * 10000.0) * 1.0E-4;
      return v;
   }

   public static Quaternionf fixQuaternion(Quaternionf q) {
      if (isSimilar(q.x, 0.0F)) {
         q.x = 0.0F;
      }

      if (isSimilar(q.y, 0.0F)) {
         q.y = 0.0F;
      }

      if (isSimilar(q.z, 0.0F)) {
         q.z = 0.0F;
      }

      if (isSimilar(q.w, 0.0F)) {
         q.w = 0.0F;
      }

      return q;
   }

   public static Quaterniond fixQuaternion(Quaterniond q) {
      if (isSimilar(q.x, 0.0)) {
         q.x = 0.0;
      }

      if (isSimilar(q.y, 0.0)) {
         q.y = 0.0;
      }

      if (isSimilar(q.z, 0.0)) {
         q.z = 0.0;
      }

      if (isSimilar(q.w, 0.0)) {
         q.w = 0.0;
      }

      return q;
   }

   public static Vector3f wrap(Float[] vec) {
      return new Vector3f(vec[0], vec[1], vec[2]);
   }

   public static float[] unwrap(Vector3f vec) {
      return new float[]{vec.x, vec.y, vec.z};
   }

   public static float[] unwrap(Vector3d vec) {
      return new float[]{(float)vec.x, (float)vec.y, (float)vec.z};
   }

   public static Transformation createIdentityTransform() {
      return new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1.0F), new Quaternionf());
   }

   public static Vector3f transform(Matrix4f transform, Vector3f pos) {
      Vector4f vec = transform.transform(new Vector4f(pos, 1.0F));
      return new Vector3f(vec.x, vec.y, vec.z);
   }

   public static void clampedScale(Matrix4f mat, Vector3f scale) {
      if (LOWER_SCALE_LIMIT == 0.0) {
         mat.scale(scale);
      } else {
         float sx = clampedScale(scale.x);
         float sy = clampedScale(scale.y);
         float sz = clampedScale(scale.z);
         float m00 = mat.m00() * sx;
         float m10 = mat.m10() * sy;
         float m20 = mat.m20() * sz;
         if (!(Vector3f.lengthSquared(m00, m10, m20) > LOWER_SCALE_LIMIT * LOWER_SCALE_LIMIT)) {
            m00 = clampedScale(m00);
            m10 = clampedScale(m10);
            m20 = clampedScale(m20);
         }

         float m01 = mat.m01() * sx;
         float m11 = mat.m11() * sy;
         float m21 = mat.m21() * sz;
         if (!(Vector3f.lengthSquared(m01, m11, m21) > LOWER_SCALE_LIMIT * LOWER_SCALE_LIMIT)) {
            m01 = clampedScale(m01);
            m11 = clampedScale(m11);
            m21 = clampedScale(m21);
         }

         float m02 = mat.m02() * sx;
         float m12 = mat.m12() * sy;
         float m22 = mat.m22() * sz;
         if (!(Vector3f.lengthSquared(m02, m12, m22) > LOWER_SCALE_LIMIT * LOWER_SCALE_LIMIT)) {
            m02 = clampedScale(m02);
            m12 = clampedScale(m12);
            m22 = clampedScale(m22);
         }

         mat.set(
            m00,
            m01,
            m02,
            mat.m03() * scale.x,
            m10,
            m11,
            m12,
            mat.m13() * scale.y,
            m20,
            m21,
            m22,
            mat.m23() * scale.z,
            mat.m30(),
            mat.m31(),
            mat.m32(),
            mat.m33()
         );
      }
   }

   private static float clampedScale(float val) {
      float sign = org.joml.Math.signum(val);
      return sign == 0.0F ? LOWER_SCALE_LIMIT : sign * org.joml.Math.max(org.joml.Math.abs(val), LOWER_SCALE_LIMIT);
   }

   public static Vector3f zerofyScale(Vector3f vec) {
      return LOWER_SCALE_LIMIT == 0.0
         ? vec
         : new Vector3f(
            LOWER_SCALE_LIMIT >= org.joml.Math.abs(vec.x) ? 0.0F : vec.x,
            LOWER_SCALE_LIMIT >= org.joml.Math.abs(vec.y) ? 0.0F : vec.y,
            LOWER_SCALE_LIMIT >= org.joml.Math.abs(vec.z) ? 0.0F : vec.z
         );
   }

   public static Vector3f zerofyScale(Vector3f vec, Vector3f dest) {
      return LOWER_SCALE_LIMIT == 0.0
         ? dest.set(vec)
         : dest.set(
            LOWER_SCALE_LIMIT >= org.joml.Math.abs(vec.x) ? 0.0F : vec.x,
            LOWER_SCALE_LIMIT >= org.joml.Math.abs(vec.y) ? 0.0F : vec.y,
            LOWER_SCALE_LIMIT >= org.joml.Math.abs(vec.z) ? 0.0F : vec.z
         );
   }

   public static String toString(EulerAngle angle) {
      return String.format("[%s, %s, %s]", org.joml.Math.toDegrees(angle.getX()), org.joml.Math.toDegrees(angle.getY()), org.joml.Math.toDegrees(angle.getZ()));
   }

   public static UUID parseUUID(String uuid) {
      try {
         return UUID.fromString(uuid);
      } catch (IllegalArgumentException e) {
         if (uuid.length() != 32) {
            throw e;
         }

         long first = Long.parseUnsignedLong(uuid.substring(0, 16), 16);
         long last = Long.parseUnsignedLong(uuid.substring(16), 16);
         return new UUID(first, last);
      }
   }

   static {
      ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> LOWER_SCALE_LIMIT = (float)ConfigProperty.LOWER_SCALE_LIMIT.getDouble());
   }

   public enum SlerpMode {
      SLERP,
      ONLERP;

      public static TMath.SlerpMode get(String name) {
         try {
            return valueOf(name);
         } catch (IllegalArgumentException ignored) {
            return SLERP;
         }
      }
   }
}
