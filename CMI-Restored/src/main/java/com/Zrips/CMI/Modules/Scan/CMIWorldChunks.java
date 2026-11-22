package com.Zrips.CMI.Modules.Scan;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Container.CMIVectorInt2D;
import org.bukkit.World;

public class CMIWorldChunks {
  protected World world;
  protected Map<Long, BitSet> regionChunks = Collections.synchronizedMap(new HashMap<>());
  protected long totalChunks = 0L;

  public CMIWorldChunks(World newWorld) {
    this.world = newWorld;
  }

  public CMIWorldChunks clone() {
    CMIWorldChunks cmiWorldChunks = new CMIWorldChunks(this.world);
    cmiWorldChunks.regionChunks = new HashMap<>(this.regionChunks);
    cmiWorldChunks.totalChunks = this.totalChunks;
    return cmiWorldChunks;
  }

  public void setRegion(Map<Long, BitSet> map) {
    this.regionChunks = map;
  }

  public static long packRegionCoords(CMIVectorInt2D cmiVectorInt2D) {
    return (long) cmiVectorInt2D.getX() << 32 | cmiVectorInt2D.getZ() & 4294967295L;
  }

  public static long packRegionCoords(int value, int z) {
    return (long) value << 32 | z & 4294967295L;
  }

  public static int getRegionX(long longValue) {
    return (int) (longValue >> 32);
  }

  public static int getRegionZ(long longValue) {
    return (int) longValue;
  }

  public static CMIVectorInt2D getRegion(long longValue) {
    return new CMIVectorInt2D(getRegionX(longValue), getRegionZ(longValue));
  }

  public World getWorld() {
    return this.world;
  }

  public void setRegionChunks(CMIVectorInt2D cmiVectorInt2D, BitSet bitSet) {
    this.setRegionChunks(packRegionCoords(cmiVectorInt2D), bitSet);
  }

  public void setRegionChunks(long longValue, BitSet bitSet) {
    this.regionChunks.put(longValue, bitSet);
  }

  public Map<Long, BitSet> getRegionChunks() {
    return this.regionChunks;
  }

  public boolean getRegionsChunk(CMIVectorInt2D cmiVectorInt2D, int value) {
    return this.getRegionsChunk(packRegionCoords(cmiVectorInt2D), value);
  }

  public boolean getRegionsChunk(long longValue, int value) {
    BitSet bitSet = this.regionChunks.get(longValue);
    return bitSet != null && value < bitSet.size() ? bitSet.get(value) : false;
  }

  public BitSet getRegionChunks(CMIVectorInt2D cmiVectorInt2D) {
    return this.getRegionChunks(packRegionCoords(cmiVectorInt2D));
  }

  public BitSet getRegionChunks(long longValue) {
    return this.regionChunks.get(longValue);
  }

  @Nullable
  public BitSet getChunks(long longValue) {
    return this.regionChunks.get(longValue);
  }

  public long getTotalChunks() {
    return this.totalChunks;
  }

  public void setTotalChunks(long longValue) {
    this.totalChunks = longValue;
  }

  public void addTotalChunks(int value) {
    this.totalChunks += value;
  }

  public void addTotalChunks() {
    this.totalChunks++;
  }
}
