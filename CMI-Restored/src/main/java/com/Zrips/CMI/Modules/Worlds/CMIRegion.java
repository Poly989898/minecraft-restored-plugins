package com.Zrips.CMI.Modules.Worlds;

import java.util.TreeMap;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class CMIRegion {
  private int z = 0;
  private int x = 0;
  private World world;
  private TreeMap<Integer, CMIChunk> chunks = new TreeMap<>();

  public CMIRegion(Location location) {
    this(location.getWorld(), location.getChunk().getX() >> 5, location.getChunk().getZ() >> 5);
  }

  public CMIRegion(Chunk chunk) {
    this(chunk.getWorld(), chunk.getX() >> 5, chunk.getZ() >> 5);
  }

  public CMIRegion(World regionWorld, int value, int regionZ) {
    this.x = value;
    this.z = regionZ;
    this.world = regionWorld;
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

  public CMIChunk getChunk(Chunk chunk) {
    return this.getChunk(chunk.getX(), chunk.getZ()).setGenerated(true).recheckBiomes();
  }

  public Integer getPlace() {
    return this.x + this.z * 1024;
  }

  public CMIChunk getRelativeChunk(int value, int relativeZ) {
    return this.getChunk(this.x * 32 + value, this.z * 32 + relativeZ);
  }

  private int getChunkRelativePlace(int value, int relativeZ) {
    value -= this.x * 32;
    relativeZ -= this.z * 32;
    return value + relativeZ * 32;
  }

  public boolean exists(int value) {
    CMIChunk cmiChunk = this.chunks.get(value);
    return cmiChunk != null;
  }

  public boolean exists(int value, int relativeZ) {
    CMIChunk cmiChunk = this.chunks.get(this.getChunkRelativePlace(value, relativeZ));
    return cmiChunk != null;
  }

  public CMIChunk getChunk(int value, int relativeZ) {
    return this.chunks.get(this.getChunkRelativePlace(value, relativeZ));
  }

  public TreeMap<Integer, CMIChunk> getChunks() {
    return this.chunks;
  }

  public void setChunks(TreeMap<Integer, CMIChunk> treeMap) {
    this.chunks = treeMap;
  }

  public CMIChunk addChunk(World chunkWorld, int value, int chunkZ) {
    CMIChunk cmiChunk = new CMIChunk(chunkWorld, value, chunkZ);
    this.chunks.put(cmiChunk.getPlace(), cmiChunk);
    return cmiChunk;
  }

  public CMIChunk addChunk(Chunk chunk) {
    CMIChunk cmiChunk = new CMIChunk(chunk);
    this.chunks.put(cmiChunk.getPlace(), cmiChunk);
    return cmiChunk;
  }

  public World getWorld() {
    return this.world;
  }

  public void setWorld(World newWorld) {
    this.world = newWorld;
  }
}
