package net.Zrips.CMILib.Container;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CMIVectorInt3D extends CMIVectorInt2D {
  private int y = 0;

  public CMIVectorInt3D(int x, int y, int z) {
    super(x, z);
    this.y = y;
  }

  public CMIVectorInt3D(Vector vector) {
    this(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
  }

  public CMIVectorInt3D(Location loc) {
    this(loc.toVector());
  }

  public CMIVectorInt3D(CMILocation loc) {
    this(loc.toVector());
  }

  public CMIVectorInt3D add(CMIVectorInt3D v) {
    return this.plus(v);
  }

  public CMIVectorInt3D plus(CMIVectorInt3D v) {
    this.x = this.x + v.x;
    this.y = this.y + v.y;
    this.z = this.z + v.z;
    return this;
  }

  public CMIVectorInt3D minus(CMIVectorInt3D v) {
    this.x = this.x - v.x;
    this.y = this.y - v.y;
    this.z = this.z - v.z;
    return this;
  }

  public CMIVectorInt3D times(int s) {
    this.x *= s;
    this.y *= s;
    this.z *= s;
    return this;
  }

  public double dot(CMIVectorInt3D v) {
    return this.x * v.x + this.y * v.y + this.z * v.z;
  }

  public int getY() {
    return this.y;
  }

  public void setY(int y) {
    this.y = y;
  }

  @Override
  public String toString() {
    return this.getX() + ";" + this.getY() + ";" + this.getZ();
  }

  public CMIVectorInt3D clone() {
    return new CMIVectorInt3D(this.x, this.y, this.z);
  }

  public Location toLocation(World world) {
    return new Location(world, this.getX(), this.getY(), this.getZ());
  }
}
