package com.Zrips.CMI.Modules.Region;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class CMIRegion {
  private int z = 0;
  private int x = 0;

  public CMIRegion(Location location) {
    this.x = location.getChunk().getX();
    this.z = location.getChunk().getZ();
  }

  public CMIRegion(Chunk chunk) {
    this.x = chunk.getX();
    this.z = chunk.getZ();
  }

  public CMIRegion(int value, int zValue) {
    this.x = value;
    this.z = zValue;
  }

  public int getX() {
    return this.x;
  }

  public CMIRegion setX(int value) {
    this.x = value;
    return this;
  }

  public int getZ() {
    return this.z;
  }

  public CMIRegion setZ(int value) {
    this.z = value;
    return this;
  }

  @Override
  public String toString() {
    return this.x + ";" + this.z;
  }
}
