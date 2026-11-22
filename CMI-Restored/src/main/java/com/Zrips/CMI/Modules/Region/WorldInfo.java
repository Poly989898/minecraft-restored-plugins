package com.Zrips.CMI.Modules.Region;

import com.Zrips.CMI.CMI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldInfo {
  private transient Map<String, List<Boolean>> regionChunks =
      Collections.synchronizedMap(new HashMap<>());
  private CMIRegion center;
  private long totalChunks = 0L;
  private long lastChecked = -1L;
  private World world;
  private int range = 0;

  public WorldInfo clone() {
    WorldInfo worldInfo = new WorldInfo(this.world, this.center);
    worldInfo.setRegion(new HashMap<>(this.regionChunks));
    worldInfo.setTotalChunks(this.totalChunks);
    return worldInfo;
  }

  public WorldInfo(World newWorld, CMIRegion cmiRegion) {
    this.world = newWorld;
    this.center = cmiRegion;
  }

  public WorldInfo(Location location, int value) {
    this.world = location.getWorld();
    this.center = new CMIRegion(location);
    this.range = value;
  }

  public void setRegion(Map<String, List<Boolean>> map) {
    this.regionChunks = map;
  }

  public Map<String, List<Boolean>> getRegion() {
    return this.regionChunks;
  }

  private static String toString(CMIRegion cmiRegion) {
    return cmiRegion.getX() + ":" + cmiRegion.getZ();
  }

  public Boolean getRegionsChunk(CMIRegion cmiRegion, int value) {
    var list = this.regionChunks.get(toString(cmiRegion));
    return list == null ? null : (Boolean) list.get(value);
  }

  public List<Boolean> getRegionPlaces(CMIRegion cmiRegion) {
    return this.regionChunks.get(toString(cmiRegion));
  }

  public WorldInfo setRegionPlaces(CMIRegion cmiRegion, List<Boolean> list) {
    this.regionChunks.put(toString(cmiRegion), list);
    return this;
  }

  public CMIRegion getNextChunkLoc(boolean flag) {
    return CMI.getInstance().getRegionManager().getNextChunkLocation(this, flag);
  }

  public Chunk getNextChunk(boolean flag) {
    return CMI.getInstance().getRegionManager().getNextChunk(this, flag);
  }

  public void setRegionChunks(CMIRegion cmiRegion, List<Boolean> list) {
    this.regionChunks.put(toString(cmiRegion), list);
  }

  public CMIRegion getCenter() {
    if (this.center == null) {
      this.center = new CMIRegion(0, 0);
    }

    return this.center;
  }

  public void setCenter(CMIRegion cmiRegion) {
    this.center = cmiRegion;
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

  public World getWorld() {
    return this.world;
  }

  public void setWorld(World newWorld) {
    this.world = newWorld;
  }

  public Integer getRange() {
    return this.range == 0 ? null : this.range;
  }

  public void setRange(Integer integer) {
    this.range = integer == null ? 0 : integer;
  }
}
