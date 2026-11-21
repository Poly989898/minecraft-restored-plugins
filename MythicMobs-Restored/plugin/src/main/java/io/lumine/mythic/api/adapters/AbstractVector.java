package io.lumine.mythic.api.adapters;

import com.google.common.base.Preconditions;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import org.joml.Vector3f;

public class AbstractVector implements Cloneable {
   private static final double epsilon = 1.0E-6;
   protected double x;
   protected double y;
   protected double z;

   public AbstractVector(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public AbstractVector(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public AbstractVector(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public AbstractVector(String input) {
      try {
         String[] splitStr = input.split(",");
         if (splitStr.length == 1) {
            this.x = Double.parseDouble(input);
            this.y = Double.parseDouble(input);
            this.z = Double.parseDouble(input);
         } else {
            this.x = Double.parseDouble(splitStr[0]);
            this.y = Double.parseDouble(splitStr[1]);
            this.z = Double.parseDouble(splitStr[2]);
         }
      } catch (Throwable e) {
         MythicLogger.error("Error in the AbstractVector constructor", e);
         throw new IllegalArgumentException("AbstractVector input must be in the format x,y,z");
      }
   }

   public AbstractVector() {
      this.x = 0.0;
      this.y = 0.0;
      this.z = 0.0;
   }

   public Vector3f toVector3f() {
      return new Vector3f(Numbers.toFloat(this.x), Numbers.toFloat(this.y), Numbers.toFloat(this.z));
   }

   public AbstractVector add(AbstractVector vec) {
      this.x = this.x + vec.x;
      this.y = this.y + vec.y;
      this.z = this.z + vec.z;
      return this;
   }

   public AbstractVector subtract(AbstractVector vec) {
      this.x = this.x - vec.x;
      this.y = this.y - vec.y;
      this.z = this.z - vec.z;
      return this;
   }

   public AbstractVector multiply(AbstractVector vec) {
      this.x = this.x * vec.x;
      this.y = this.y * vec.y;
      this.z = this.z * vec.z;
      return this;
   }

   public AbstractVector multiply(int m) {
      this.x *= m;
      this.y *= m;
      this.z *= m;
      return this;
   }

   public AbstractVector multiply(double m) {
      this.x *= m;
      this.y *= m;
      this.z *= m;
      return this;
   }

   public AbstractVector multiply(float m) {
      this.x *= m;
      this.y *= m;
      this.z *= m;
      return this;
   }

   public AbstractVector divide(AbstractVector vec) {
      this.x = this.x / vec.x;
      this.y = this.y / vec.y;
      this.z = this.z / vec.z;
      return this;
   }

   public AbstractVector copy(AbstractVector vec) {
      this.x = vec.x;
      this.y = vec.y;
      this.z = vec.z;
      return this;
   }

   public double length() {
      return Math.sqrt(Numbers.square(this.x) + Numbers.square(this.y) + Numbers.square(this.z));
   }

   public double lengthSquared() {
      return Numbers.square(this.x) + Numbers.square(this.y) + Numbers.square(this.z);
   }

   public double getX() {
      return this.x;
   }

   public int getBlockX() {
      return Numbers.floor(this.x);
   }

   public double getY() {
      return this.y;
   }

   public int getBlockY() {
      return Numbers.floor(this.y);
   }

   public double getZ() {
      return this.z;
   }

   public int getBlockZ() {
      return Numbers.floor(this.z);
   }

   public AbstractVector setX(int x) {
      this.x = x;
      return this;
   }

   public AbstractVector setX(double x) {
      this.x = x;
      return this;
   }

   public AbstractVector setX(float x) {
      this.x = x;
      return this;
   }

   public AbstractVector setY(int y) {
      this.y = y;
      return this;
   }

   public AbstractVector setY(double y) {
      this.y = y;
      return this;
   }

   public AbstractVector setY(float y) {
      this.y = y;
      return this;
   }

   public AbstractVector setZ(int z) {
      this.z = z;
      return this;
   }

   public AbstractVector setZ(double z) {
      this.z = z;
      return this;
   }

   public AbstractVector setZ(float z) {
      this.z = z;
      return this;
   }

   public AbstractVector normalize() {
      double length = this.length();
      this.x /= length;
      this.y /= length;
      this.z /= length;
      return this;
   }

   public AbstractVector crossProduct(AbstractVector other) {
      return new AbstractVector(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x);
   }

   public AbstractVector rotate(double degrees) {
      double rad = Math.toRadians(degrees);
      double sin = Math.sin(rad);
      double cos = Math.cos(rad);
      double x = this.getX() * cos - this.getZ() * sin;
      double z = this.getX() * sin + this.getZ() * cos;
      this.setX(x);
      this.setZ(z);
      return this;
   }

   public void rotate(AbstractVector axis, double angle) {
      double sinHalfAngle = Math.sin(angle / 2.0);
      double cosHalfAngle = Math.cos(angle / 2.0);
      double rx = axis.x * sinHalfAngle;
      double ry = axis.y * sinHalfAngle;
      double rz = axis.z * sinHalfAngle;
      AbstractVector rotation = new AbstractVector(rx, ry, rz);
      AbstractVector conjugate = new AbstractVector(-rx, -ry, -rz);
      AbstractVector w = rotation.crossProduct(this).add(this.multiply(cosHalfAngle));
      AbstractVector rotatedVector = w.crossProduct(conjugate);
      this.x = rotatedVector.x;
      this.y = rotatedVector.y;
      this.z = rotatedVector.z;
   }

   public void rotate(AbstractVector direction) {
      AbstractVector axis = this.crossProduct(direction).normalize();
      double angle = Math.acos(this.normalize().dot(direction));
      this.rotateAroundAxis(axis, angle);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof AbstractVector other)
         ? false
         : Math.abs(this.x - other.x) < 1.0E-6
            && Math.abs(this.y - other.y) < 1.0E-6
            && Math.abs(this.z - other.z) < 1.0E-6
            && this.getClass().equals(obj.getClass());
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 79 * hash + (int)(Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
      hash = 79 * hash + (int)(Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
      return 79 * hash + (int)(Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
   }

   public AbstractVector clone() {
      try {
         return (AbstractVector)super.clone();
      } catch (CloneNotSupportedException e) {
         throw new Error(e);
      }
   }

   @Override
   public String toString() {
      return this.x + "," + this.y + "," + this.z;
   }

   public AbstractLocation toLocation(AbstractWorld world) {
      return new AbstractLocation(world, this.x, this.y, this.z);
   }

   public AbstractLocation toLocation(AbstractWorld world, float yaw, float pitch) {
      return new AbstractLocation(world, this.x, this.y, this.z, yaw, pitch);
   }

   public static double getEpsilon() {
      return 1.0E-6;
   }

   public double dot(AbstractVector other) {
      return this.x * other.x + this.y * other.y + this.z * other.z;
   }

   public float angle(AbstractVector other) {
      double dot = this.dot(other) / (this.length() * other.length());
      return (float)Math.acos(dot);
   }

   public AbstractVector rotateAroundX(double angle) {
      double angleCos = Math.cos(angle);
      double angleSin = Math.sin(angle);
      double y = angleCos * this.getY() - angleSin * this.getZ();
      double z = angleSin * this.getY() + angleCos * this.getZ();
      return this.setY(y).setZ(z);
   }

   public AbstractVector rotateAroundY(double angle) {
      double angleCos = Math.cos(angle);
      double angleSin = Math.sin(angle);
      double x = angleCos * this.getX() + angleSin * this.getZ();
      double z = -angleSin * this.getX() + angleCos * this.getZ();
      return this.setX(x).setZ(z);
   }

   public AbstractVector rotateAroundZ(double angle) {
      double angleCos = Math.cos(angle);
      double angleSin = Math.sin(angle);
      double x = angleCos * this.getX() - angleSin * this.getY();
      double y = angleSin * this.getX() + angleCos * this.getY();
      return this.setX(x).setY(y);
   }

   public AbstractVector rotateAroundAxis(AbstractVector axis, double angle) throws IllegalArgumentException {
      Preconditions.checkArgument(axis != null, "The provided axis vector was null");
      return this.rotateAroundNonUnitAxis(axis.isNormalized() ? axis : axis.clone().normalize(), angle);
   }

   public AbstractVector rotateAroundNonUnitAxis(AbstractVector axis, double angle) throws IllegalArgumentException {
      Preconditions.checkArgument(axis != null, "The provided axis vector was null");
      double x = this.getX();
      double y = this.getY();
      double z = this.getZ();
      double x2 = axis.getX();
      double y2 = axis.getY();
      double z2 = axis.getZ();
      double cosTheta = Math.cos(angle);
      double sinTheta = Math.sin(angle);
      double dotProduct = this.dot(axis);
      double xPrime = x2 * dotProduct * (1.0 - cosTheta) + x * cosTheta + (-z2 * y + y2 * z) * sinTheta;
      double yPrime = y2 * dotProduct * (1.0 - cosTheta) + y * cosTheta + (z2 * x - x2 * z) * sinTheta;
      double zPrime = z2 * dotProduct * (1.0 - cosTheta) + z * cosTheta + (-y2 * x + x2 * y) * sinTheta;
      return this.setX(xPrime).setY(yPrime).setZ(zPrime);
   }

   public boolean isNormalized() {
      return Math.abs(this.lengthSquared() - 1.0) < getEpsilon();
   }

   public boolean isFinite() {
      return Double.isFinite(this.x) && Double.isFinite(this.y) && Double.isFinite(this.z);
   }
}
