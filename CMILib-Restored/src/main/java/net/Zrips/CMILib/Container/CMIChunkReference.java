package net.Zrips.CMILib.Container;

import org.bukkit.Location;

public class CMIChunkReference {
  private final int z;
  private final int x;

  public static int getChunkCoord(int val) {
    return val >> 4;
  }

  public CMIChunkReference(Location loc) {
    this.x = getChunkCoord(loc.getBlockX());
    this.z = getChunkCoord(loc.getBlockZ());
  }

  public CMIChunkReference(int x, int z) {
    this.x = x;
    this.z = z;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj != null && this.getClass() == obj.getClass()) {
      CMIChunkReference other = (CMIChunkReference) obj;
      return this.x == other.x && this.z == other.z;
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.x ^ this.z;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.x).append(";").append(this.z);
    return sb.toString();
  }
}
