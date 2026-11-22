package net.Zrips.CMILib.Container;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CMIVector3D extends CMIVector2D {
  private double z = 0.0;

  public CMIVector3D() {
    super(0.0, 0.0);
  }

  public CMIVector3D(Vector v) {
    super(v.getX(), v.getY());
    this.z = v.getZ();
  }

  public CMIVector3D(double x, double y, double z) {
    super(x, y);
    this.z = z;
  }

  public CMIVector3D add(double x, double y, double z) {
    return this.plus(x, y, z);
  }

  public CMIVector3D plus(double x, double y, double z) {
    this.x += x;
    this.y += y;
    this.z += z;
    return this;
  }

  public CMIVector3D add(CMIVector3D v) {
    return this.plus(v.x, v.y, v.z);
  }

  public CMIVector3D plus(CMIVector3D v) {
    return this.plus(v.x, v.y, v.z);
  }

  public CMIVector3D minus(CMIVector3D v) {
    return this.minus(v.getX(), v.getY(), v.getZ());
  }

  public CMIVector3D minus(Vector v) {
    return this.minus(v.getX(), v.getY(), v.getZ());
  }

  public CMIVector3D minus(double x, double y, double z) {
    this.x -= x;
    this.y -= y;
    this.z -= z;
    return this;
  }

  public double dot(CMIVector3D v) {
    return this.x * v.x + this.y * v.y + this.z * v.z;
  }

  public CMIVector3D cross(CMIVector3D other) {
    double nx = this.y * other.z - this.z * other.y;
    double ny = this.z * other.x - this.x * other.z;
    double nz = this.x * other.y - this.y * other.x;
    this.x = nx;
    this.y = ny;
    this.z = nz;
    return this;
  }

  public CMIVector3D multiply(double scalar) {
    return this.multiply(scalar, scalar, scalar);
  }

  public CMIVector3D multiply(CMIVector3D scale) {
    return this.multiply(scale.getX(), scale.getY(), scale.getZ());
  }

  public CMIVector3D multiply(double x, double y, double z) {
    this.x *= x;
    this.y *= y;
    this.z *= z;
    return this;
  }

  public CMIVector3D normalize() {
    double length = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    this.x /= length;
    this.y /= length;
    this.z /= length;
    return this;
  }

  public double lengthSquared() {
    return this.x * this.x + this.y * this.y + this.z * this.z;
  }

  @Override
  public String toString() {
    return super.toString() + ";" + CMINumber.formatNumber(this.z);
  }

  public static CMIVector3D fromString(String value) {
    String[] parts = value.split(";");
    if (parts.length != 3) {
      return new CMIVector3D(0.0, 0.0, 0.0);
    }

    try {
      return new CMIVector3D(
          Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    } catch (Exception e) {
      e.printStackTrace();
      return new CMIVector3D(0.0, 0.0, 0.0);
    }
  }

  public double getZ() {
    return this.z;
  }

  public CMIVector3D setZ(double z) {
    this.z = z;
    return this;
  }

  public Location toLocation(World world) {
    return new Location(world, this.getX(), this.getY(), this.getZ());
  }

  public boolean isZero() {
    return this.x == 0.0 && this.y == 0.0 && this.z == 0.0;
  }

  public CMIVector3D clone() {
    return new CMIVector3D(this.x, this.y, this.z);
  }

  public Vector toVector() {
    return new Vector(this.x, this.y, this.z);
  }

  @Override
  public String translateVariables(String text) {
    return text.replace("[x]", CMINumber.format2(this.getX()))
        .replace("[y]", CMINumber.format2(this.getY()))
        .replace("[z]", CMINumber.format2(this.getZ()));
  }
}
