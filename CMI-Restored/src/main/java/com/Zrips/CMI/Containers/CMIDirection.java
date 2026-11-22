package com.Zrips.CMI.Containers;

import net.Zrips.CMILib.CMILib;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public enum CMIDirection {
  N("North", 0, 0, -1),
  NE("North_East", 45, 1, -1),
  E("East", 90, 1, 0),
  SE("South_East", 135, 1, 1),
  S("South", 180, 0, 1),
  SW("South_West", 225, -1, 1),
  W("West", 270, -1, 0),
  NW("North_West", 315, -1, 1);

  private int angle;
  private String name;
  private int x = 0;
  private int z = 0;

  CMIDirection(String string, int value, int startAngle, int endAngle) {
    this.name = string;
    this.angle = value;
    this.x = startAngle;
    this.z = endAngle;
  }

  CMIDirection(String string, int value) {
    this.name = string;
    this.angle = value;
  }

  public static CMIDirection getFromAngle(double number) {
    CMIDirection[] cmiDirections;
    int value = (cmiDirections = values()).length;

    for (int index = 0; index < value; index++) {
      CMIDirection cmiDirection = cmiDirections[index];
      double delta = cmiDirection.getAngle() - number;
      delta = delta < 0.0 ? -delta : delta;
      if (delta <= 22.5) {
        return cmiDirection;
      }
    }

    return N;
  }

  public int getAngle() {
    return this.angle;
  }

  public String getName() {
    return this.name;
  }

  public static Location rotateOnAxisDistance(
      Location location, double number, double y, double z) {
    return location.clone().add(rotateOnAxisDistanceVector(location, number, y, z));
  }

  public static Vector rotateOnAxisDistanceVector(
      Location location, double number, double x, double zCoord) {
    double yawRad = location.getYaw() / 180.0 * Math.PI;
    double pitchRad = location.getPitch() / 180.0 * Math.PI;
    double radX = Math.toRadians(x);
    Vector vector =
        new Vector(
            Math.sin(radX) * number * Math.cos((Math.PI / 180.0) * zCoord),
            Math.cos(radX) * number,
            Math.sin(radX) * number * Math.sin((Math.PI / 180.0) * zCoord));
    double pitchedY = Math.cos(pitchRad) * vector.getY() - Math.sin(pitchRad) * vector.getZ();
    double pitchedZ = Math.sin(pitchRad) * vector.getY() + Math.cos(pitchRad) * vector.getZ();
    vector.setY(pitchedY).setZ(pitchedZ);
    double finalX = Math.cos(-yawRad) * vector.getX() + Math.sin(-yawRad) * vector.getZ();
    double finalZ = -Math.sin(-yawRad) * vector.getX() + Math.cos(-yawRad) * vector.getZ();
    vector.setX(finalX).setZ(finalZ);
    return vector;
  }

  public static float getAngle(Location location, Location other) {
    Vector vector = location.toVector().subtract(other.toVector());
    Location rotated = location.clone();
    rotated.setDirection(vector);
    return (rotated.getYaw() + 540.0F) % 360.0F;
  }

  public static float getAngle(float floatValue) {
    return (floatValue + 540.0F) % 360.0F;
  }

  public static CMIDirection getFromLocations(Location location, Location other) {
    return getFromAngle(getAngle(location, other));
  }

  public String getTranslatedName() {
    return CMILib.getInstance()
        .getLM()
        .getMessage("direction." + this.toString().toLowerCase(), new Object[0]);
  }

  public int getX() {
    return this.x;
  }

  public int getZ() {
    return this.z;
  }

  public CMIDirection rotate90ClockWise() {
    int value = this.getAngle() + 90;
    value = value > 360 ? 360 - value : value;
    return getFromAngle(value);
  }

  public CMIDirection rotate90CounterClockWise() {
    int value = this.getAngle() - 90;
    value = value < 0 ? 360 + value : value;
    return getFromAngle(value);
  }
}
