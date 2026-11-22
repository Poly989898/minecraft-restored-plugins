package com.Zrips.CMI.Modules.Afk;

import org.bukkit.Location;

public class checkInfo {
  private Long time = 0L;
  private Location loc;
  private double prevAngle = 0.0;

  checkInfo(Location location) {
    this.loc = location;
    this.updateTime();
  }

  public void updateTime() {
    this.time = System.currentTimeMillis();
  }

  public void updateLocation(Location location) {
    this.loc = location.clone();
  }

  public boolean isSameDirection(Location location) {
    return this.loc.getPitch() == location.getPitch() && this.loc.getYaw() == location.getYaw();
  }

  public boolean isOnlyPitchChange(Location location) {
    return this.loc.getX() == location.getX()
        && this.loc.getY() == location.getY()
        && this.loc.getZ() == location.getZ();
  }

  public boolean timeToCheck() {
    return this.time + 400L < System.currentTimeMillis();
  }

  public Long getTime() {
    return this.time;
  }

  public Location getLocation() {
    return this.loc;
  }

  public double getPrevAngle() {
    return this.prevAngle;
  }

  public void setPrevAngle(double number) {
    this.prevAngle = number;
  }
}
