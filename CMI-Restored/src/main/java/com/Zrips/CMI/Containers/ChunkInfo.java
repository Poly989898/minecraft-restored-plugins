package com.Zrips.CMI.Containers;

import org.bukkit.World;

public class ChunkInfo {
  private int x;
  private int z;
  private World w;

  public ChunkInfo(int value, int zValue) {
    this.x = value;
    this.z = zValue;
  }

  public int getX() {
    return this.x;
  }

  public int getZ() {
    return this.z;
  }

  public World getW() {
    return this.w;
  }

  public void setW(World world) {
    this.w = world;
  }
}
