package com.Zrips.CMI.Modules.Mirror;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CMIMirrorBlockInformation {
  private Vector vector;
  private Object data;

  CMIMirrorBlockInformation(Vector vector, Object object) {
    this.vector = vector;
    this.data = object;
  }

  public Location getLoc(World world) {
    return this.vector.toLocation(world);
  }

  public void setVector(Vector vector) {
    this.vector = vector;
  }

  public Object getData() {
    return this.data;
  }

  public void setData(Object object) {
    this.data = object;
  }
}
