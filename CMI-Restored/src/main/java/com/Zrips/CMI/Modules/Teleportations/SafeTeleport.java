package com.Zrips.CMI.Modules.Teleportations;

import org.bukkit.Location;

public class SafeTeleport {
  private Location old;
  private Location safeLoc;
  private Location lastCheckedLoc;
  private CMITeleportCondition TpCond;
  private boolean enableFly;
  private boolean forceDisableFly;

  public SafeTeleport(
      Location location,
      Location safeLocation,
      Location lastCheckedLocation,
      CMITeleportCondition cmiTeleportCondition) {
    this(location, safeLocation, lastCheckedLocation, cmiTeleportCondition, false);
  }

  public SafeTeleport(
      Location location, Location safeLocation, CMITeleportCondition cmiTeleportCondition) {
    this(location, safeLocation, cmiTeleportCondition, false);
  }

  public SafeTeleport(
      Location location,
      Location safeLocation,
      CMITeleportCondition cmiTeleportCondition,
      boolean flag) {
    this(location, safeLocation, null, cmiTeleportCondition, flag);
  }

  public SafeTeleport(
      Location location,
      Location safeLocation,
      Location lastCheckedLocation,
      CMITeleportCondition cmiTeleportCondition,
      boolean flag) {
    this.old = location;
    this.safeLoc = safeLocation;
    this.TpCond = cmiTeleportCondition;
    this.enableFly = flag;
    this.lastCheckedLoc = lastCheckedLocation;
  }

  public Location getOld() {
    return this.old;
  }

  public Location getSafeLoc() {
    return this.safeLoc == null ? this.old : this.safeLoc;
  }

  public CMITeleportCondition getTpCondition() {
    return this.TpCond;
  }

  public boolean isEnableFly() {
    return this.enableFly;
  }

  public SafeTeleport setEnableFly(boolean flag) {
    this.enableFly = flag;
    return this;
  }

  public boolean isForceDisableFly() {
    return this.forceDisableFly;
  }

  public void setForceDisableFly(boolean flag) {
    this.forceDisableFly = flag;
  }

  public Location getLastCheckedLoc() {
    return this.lastCheckedLoc;
  }

  public void setLastCheckedLoc(Location location) {
    this.lastCheckedLoc = location;
  }
}
