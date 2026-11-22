package com.Zrips.CMI.Modules.Portals;

import org.bukkit.util.Vector;

public class CMIVector2D {
  protected double x;
  protected double y;

  public CMIVector2D(Vector vector) {
    this.x = vector.getX();
    this.y = vector.getY();
  }

  public CMIVector2D(double number, double yValue) {
    this.x = number;
    this.y = yValue;
  }

  public CMIVector2D plus(CMIVector2D cmiVector2D) {
    return new CMIVector2D(this.x + cmiVector2D.x, this.y + cmiVector2D.y);
  }

  public CMIVector2D minus(CMIVector2D cmiVector2D) {
    return new CMIVector2D(this.x - cmiVector2D.x, this.y - cmiVector2D.y);
  }

  public CMIVector2D minus(Vector vector) {
    return new CMIVector2D(this.x - vector.getX(), this.y - vector.getY());
  }

  public CMIVector2D multiply(double number) {
    return new CMIVector2D(number * this.x, number * this.y);
  }

  public double dot(CMIVector2D cmiVector2D) {
    return this.x * cmiVector2D.x + this.y * cmiVector2D.y;
  }

  @Override
  public String toString() {
    return String.format("%.2f;%.2f", this.x, this.y);
  }

  public static CMIVector2D fromString(String string) {
    String[] strings = string.split(";");

    try {
      return new CMIVector2D(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]));
    } catch (Exception exception) {
      exception.printStackTrace();
      return new CMIVector2D(0.0, 0.0);
    }
  }

  public double getX() {
    return this.x;
  }

  public CMIVector2D setX(double number) {
    this.x = number;
    return this;
  }

  public double getY() {
    return this.y;
  }

  public CMIVector2D setY(double number) {
    this.y = number;
    return this;
  }
}
