package com.Zrips.CMI.Modules.Worlds;

import java.util.TreeMap;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class CMIWorld {
  private World world;
  private TreeMap<Integer, CMIRegion> regions = new TreeMap<>();

  public CMIWorld(Chunk chunk) {
    this.world = chunk.getWorld();
  }

  public CMIWorld(World worldSecond) {
    this.world = worldSecond;
  }

  public World getWorld() {
    return this.world;
  }

  public CMIRegion addRegion(World worldSecond, int value, int count) {
    CMIRegion cmiRegion = new CMIRegion(worldSecond, value, count);
    this.regions.put(coordinatesToPlace(value, count), cmiRegion);
    return cmiRegion;
  }

  public CMIRegion addRegion(Chunk chunk) {
    CMIRegion cmiRegion = new CMIRegion(chunk);
    this.regions.put(coordinatesToPlace(chunk.getX() >> 5, chunk.getZ() >> 5), cmiRegion);
    return cmiRegion;
  }

  public void setWorld(World worldSecond) {
    this.world = worldSecond;
  }

  public TreeMap<Integer, CMIRegion> getRegions() {
    return this.regions;
  }

  public void setRegions(TreeMap<Integer, CMIRegion> treeMap) {
    this.regions = treeMap;
  }

  public CMIRegion getRegion(Chunk chunk) {
    return this.getRegionByChunkCoord(chunk.getX(), chunk.getZ());
  }

  public CMIRegion getRegionByChunkCoord(int value, int count) {
    return this.getRegion(value >> 5, count >> 5);
  }

  public CMIRegion getRegion(int value, int count) {
    Integer integer = coordinatesToPlace(value, count);
    return this.regions.get(integer);
  }

  private static Integer coordinatesToPlace(int value, int count) {
    return value + count * 1024;
  }

  public boolean insideWorldBorder(Location location) {
    try {
      Location locationSecond = this.world.getWorldBorder().getCenter();
      double number = this.world.getWorldBorder().getSize() / 2.0;
      double amount = locationSecond.getX() - number;
      double amountSecond = locationSecond.getX() + number;
      double amountThird = locationSecond.getZ() - number;
      double amountFourth = locationSecond.getZ() + number;
      return location.getX() >= amount
          && location.getX() < amountSecond
          && location.getZ() >= amountThird
          && location.getZ() < amountFourth;
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return true;
    }
  }

  public static int getMinHeight(World worldSecond) {
    return Version.isCurrentEqualOrLower(Version.v1_16_R3) ? 0 : worldSecond.getMinHeight();
  }
}
