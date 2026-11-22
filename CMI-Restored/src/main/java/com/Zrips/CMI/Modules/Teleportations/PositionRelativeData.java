package com.Zrips.CMI.Modules.Teleportations;

import net.Zrips.CMILib.Container.CMIWorld;
import org.bukkit.Location;
import org.bukkit.World.Environment;

public class PositionRelativeData {
  private int localX = 0;
  private int localZ = 0;
  private int maxWorldY = 0;
  private int minWorldY = 0;

  public PositionRelativeData(Location location) {
    this.localX = location.getBlockX() & 15;
    this.localZ = location.getBlockZ() & 15;
    this.maxWorldY = CMIWorld.getMaxHeight(location.getWorld());
    this.minWorldY = CMIWorld.getMinHeight(location.getWorld());
    if (location.getWorld().getEnvironment().equals(Environment.NETHER) && this.maxWorldY < 200) {
      this.maxWorldY = 255;
    }
  }

  public int getLocalX() {
    return this.localX;
  }

  public int getLocalZ() {
    return this.localZ;
  }

  public int getMaxWorldY() {
    return this.maxWorldY;
  }

  public int getMinWorldY() {
    return this.minWorldY;
  }
}
