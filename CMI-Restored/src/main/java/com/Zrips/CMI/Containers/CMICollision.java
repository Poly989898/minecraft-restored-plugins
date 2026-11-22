package com.Zrips.CMI.Containers;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class CMICollision {
  private Vector min;
  private Vector max;

  public CMICollision(CMIHitBox cmiHitBox) {
    this(cmiHitBox.getMin(), cmiHitBox.getMax());
  }

  public CMICollision(Vector vector, Vector max) {
    this(vector.getX(), vector.getY(), vector.getZ(), max.getX(), max.getY(), max.getZ());
  }

  public CMICollision(
      double number, double minX, double minY, double minZ, double maxX, double maxY) {
    this.min = new Vector(Math.min(number, minZ), Math.min(minX, maxX), Math.min(minY, maxY));
    this.max = new Vector(Math.max(number, minZ), Math.max(minX, maxX), Math.max(minY, maxY));
  }

  public Vector getMin() {
    return this.min;
  }

  public Vector getMax() {
    return this.max;
  }

  public double min(int value) {
    switch (value) {
      case 0:
        return this.min.getX();
      case 1:
        return this.min.getY();
      case 2:
        return this.min.getZ();
      default:
        return 0.0;
    }
  }

  public double max(int value) {
    switch (value) {
      case 0:
        return this.max.getX();
      case 1:
        return this.max.getY();
      case 2:
        return this.max.getZ();
      default:
        return 0.0;
    }
  }

  public boolean collides(CMIRay cmiRay, double number, double tMax) {
    for (int value = 0; value < 3; value++) {
      double inv = 1.0 / cmiRay.direction(value);
      double tEnter = (this.min(value) - cmiRay.origin(value)) * inv;
      double tExit = (this.max(value) - cmiRay.origin(value)) * inv;
      if (inv < 0.0) {
        double tNear = tEnter;
        tEnter = tExit;
        tExit = tNear;
      }

      number = tEnter > number ? tEnter : number;
      tMax = tExit < tMax ? tExit : tMax;
      if (tMax <= number) {
        return false;
      }
    }

    return true;
  }

  public double collidesNearest(CMIRay cmiRay, double number, double tMax) {
    for (int value = 0; value < 3; value++) {
      double invDirection = 1.0 / cmiRay.direction(value);
      double tEntry = (this.min(value) - cmiRay.origin(value)) * invDirection;
      double tExit = (this.max(value) - cmiRay.origin(value)) * invDirection;
      if (invDirection < 0.0) {
        double tSwap = tEntry;
        tEntry = tExit;
        tExit = tSwap;
      }

      number = tEntry > number ? tEntry : number;
      tMax = tExit < tMax ? tExit : tMax;
      if (tMax <= number) {
        return -1.0;
      }
    }

    return number;
  }

  public boolean contains(Location location) {
    if (location.getX() > this.max.getX()
        || location.getY() > this.max.getY()
        || location.getZ() > this.max.getZ()
        || location.getX() < this.min.getX()) {
      return false;
    } else {
      return location.getY() < this.min.getY() ? false : !(location.getZ() < this.min.getZ());
    }
  }
}
