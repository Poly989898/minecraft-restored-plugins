package com.Zrips.CMI.Modules.Portals;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CMIVector3D extends CMIVector2D {
  private double z = 0.0;

  public CMIVector3D() {
    super(0.0, 0.0);
  }

  public CMIVector3D(Vector vector) {
    super(vector.getX(), vector.getY());
    this.z = vector.getZ();
  }

  public CMIVector3D(double number, double yCoord, double zCoord) {
    super(number, yCoord);
    this.z = zCoord;
  }

  public CMIVector3D plus(double number, double yCoord, double zCoord) {
    return new CMIVector3D(this.x + number, this.y + yCoord, this.z + zCoord);
  }

  public CMIVector3D plus(CMIVector3D cmiVector3D) {
    return this.plus(cmiVector3D.x, cmiVector3D.y, cmiVector3D.z);
  }

  public CMIVector3D minus(CMIVector3D cmiVector3D) {
    return this.minus(cmiVector3D.getX(), cmiVector3D.getY(), cmiVector3D.getZ());
  }

  public CMIVector3D minus(Vector vector) {
    return this.minus(vector.getX(), vector.getY(), vector.getZ());
  }

  public CMIVector3D minus(double number, double yCoord, double zCoord) {
    return new CMIVector3D(this.x - number, this.y - yCoord, this.z - zCoord);
  }

  public double dot(CMIVector3D cmiVector3D) {
    return this.x * cmiVector3D.x + this.y * cmiVector3D.y + this.z * cmiVector3D.z;
  }

  public CMIVector3D cross(CMIVector3D cmiVector3D) {
    double number = this.y * cmiVector3D.z - this.z * cmiVector3D.y;
    double crossY = this.z * cmiVector3D.x - this.x * cmiVector3D.z;
    double crossZ = this.x * cmiVector3D.y - this.y * cmiVector3D.x;
    return new CMIVector3D(number, crossY, crossZ);
  }

  public CMIVector3D multiply(double number) {
    return this.multiply(number, number, number);
  }

  public CMIVector3D multiply(CMIVector3D cmiVector3D) {
    return this.multiply(cmiVector3D.getX(), cmiVector3D.getY(), cmiVector3D.getZ());
  }

  public CMIVector3D multiply(double number, double yCoord, double zCoord) {
    return new CMIVector3D(this.x * number, this.y * yCoord, this.z * zCoord);
  }

  public CMIVector3D normalize() {
    double number = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    return new CMIVector3D(this.x / number, this.y / number, this.z / number);
  }

  public double lengthSquared() {
    return this.x * this.x + this.y * this.y + this.z * this.z;
  }

  @Override
  public String toString() {
    return String.format("%.2f;%.2f;%.2f", this.x, this.y, this.z);
  }

  public static CMIVector3D fromString(String string) {
    String[] strings = string.split(";");

    try {
      return new CMIVector3D(
          Double.parseDouble(strings[0]),
          Double.parseDouble(strings[1]),
          Double.parseDouble(strings[2]));
    } catch (Exception exception) {
      exception.printStackTrace();
      return new CMIVector3D(0.0, 0.0, 0.0);
    }
  }

  public double getZ() {
    return this.z;
  }

  public CMIVector3D setZ(double number) {
    this.z = number;
    return this;
  }

  public Location toLocation(World world) {
    return new Location(world, this.getX(), this.getY(), this.getZ());
  }

  public boolean isZero() {
    return this.x == 0.0 && this.y == 0.0 && this.z == 0.0;
  }
}
