package com.Zrips.CMI.Modules.Portals;

public class RandomLoc {
  double x = 0.0;
  double y = 0.0;
  double z = 0.0;

  public RandomLoc(double number, double yValue, double zValue) {
    this.x = number;
    this.y = yValue;
    this.z = zValue;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

  public double getZ() {
    return this.z;
  }
}
