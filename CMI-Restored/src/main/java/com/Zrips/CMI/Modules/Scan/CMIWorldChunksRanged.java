package com.Zrips.CMI.Modules.Scan;

import net.Zrips.CMILib.Container.CMIVectorInt2D;
import org.bukkit.Chunk;
import org.bukkit.World;

public class CMIWorldChunksRanged extends CMIWorldChunks {
  private CMIVectorInt2D center;
  private long lastChecked = -1L;
  private int range = 0;

  public CMIWorldChunksRanged(World targetWorld, CMIVectorInt2D cmiVectorInt2D, int value) {
    super(targetWorld);
    this.range = value;
    this.center = cmiVectorInt2D;
  }

  public int getRange() {
    return this.range;
  }

  public void setRange(int value) {
    this.range = value <= 0 ? 0 : value;
  }

  public long getLastChecked() {
    return this.lastChecked;
  }

  public boolean allChecked() {
    return this.lastChecked >= this.totalChunks;
  }

  public long getLeftToScan() {
    return this.totalChunks - this.lastChecked < 0L
        ? 0L
        : this.totalChunks - (this.lastChecked < 0L ? 0L : this.lastChecked);
  }

  public long getNextPlace() {
    return this.lastChecked + 1L;
  }

  public void setLastChecked(long longValue) {
    this.lastChecked = longValue;
  }

  public void addLastChecked() {
    this.lastChecked++;
  }

  public CMIVectorInt2D getCenter() {
    if (this.center == null) {
      this.center = new CMIVectorInt2D(0, 0);
    }

    return this.center;
  }

  public void setCenter(CMIVectorInt2D cmiVectorInt2D) {
    this.center = cmiVectorInt2D;
  }

  public CMIVectorInt2D getNextInSpiral(long longValue) {
    int value = (int) Math.floor(Math.sqrt(longValue));
    int half = (int) Math.round(value / 2.0);
    double number = Math.pow(-1.0, value + 1);
    double offsetSum = value * (value + 1) - longValue;
    double offsetDiff = Math.abs(value * (value + 1) - longValue);
    int resultX =
        (int) (half * Math.pow(-1.0, value + 1) + number * (offsetSum - offsetDiff) / 2.0);
    int resultZ = (int) (half * Math.pow(-1.0, value) + number * (offsetSum + offsetDiff) / 2.0);
    return new CMIVectorInt2D(resultX, resultZ);
  }

  public CMIVectorInt2D getNextChunkLocation(boolean flag) {
    CMIVectorInt2D cmiVectorInt2D = this.getNextInSpiral(this.getNextPlace());
    CMIVectorInt2D cmiVectorInt2D2 =
        new CMIVectorInt2D(
            cmiVectorInt2D.getX() + this.getCenter().getX(),
            cmiVectorInt2D.getZ() + this.getCenter().getZ());
    CMIVectorInt2D cmiVectorInt2D3 = getRegionCord(cmiVectorInt2D2);
    CMIVectorInt2D cmiVectorInt2D4 =
        new CMIVectorInt2D(
            cmiVectorInt2D2.getX() - cmiVectorInt2D3.getX() * 32,
            cmiVectorInt2D2.getZ() - cmiVectorInt2D3.getZ() * 32);
    boolean alternateFlag =
        this.getRegionsChunk(
            cmiVectorInt2D3, coordToChunkPlace(cmiVectorInt2D4.getX(), cmiVectorInt2D4.getZ()));
    return (!flag || !alternateFlag) && (flag || alternateFlag) ? null : cmiVectorInt2D2;
  }

  protected static CMIVectorInt2D getRegionCord(CMIVectorInt2D cmiVectorInt2D) {
    return getRegionCord(cmiVectorInt2D.getX(), cmiVectorInt2D.getZ());
  }

  public static CMIVectorInt2D getRegionCord(Chunk chunk) {
    return getRegionCord(chunk.getX(), chunk.getZ());
  }

  public static CMIVectorInt2D getRegionCord(int value, int chunkZ) {
    return new CMIVectorInt2D(value >> 5, chunkZ >> 5);
  }

  public static CMIVectorInt2D getRealChunkFromPlace(CMIVectorInt2D cmiVectorInt2D, int value) {
    CMIVectorInt2D cmiVectorInt2D2 = getCordFromPlace(value);
    return new CMIVectorInt2D(
        cmiVectorInt2D.getX() * 32 + cmiVectorInt2D2.getX(),
        cmiVectorInt2D.getZ() * 32 + cmiVectorInt2D2.getZ());
  }

  protected static CMIVectorInt2D getCordFromPlace(int value) {
    return new CMIVectorInt2D(value % 32, value / 32);
  }

  protected static int coordToChunkPlace(int value, int zCoord) {
    return value + zCoord * 32;
  }

  public static int chunkCoordToPlace(Chunk chunk) {
    int value = chunk.getX() & 31;
    int localZ = chunk.getZ() & 31;
    return value + localZ * 32;
  }

  public static CMIVectorInt2D chunkPlaceToCoords(int value) {
    return new CMIVectorInt2D(value % 32, value / 32);
  }
}
