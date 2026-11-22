package com.Zrips.CMI.Modules.Region;

import com.Zrips.CMI.CMI;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Container.CMIVectorInt2D;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class WorldManager {
  private CMI plugin;
  public Pattern regionPattern = Pattern.compile("r\\.(.+)\\.(.+)\\.mca");
  private static Map<String, WorldInfo> worldMap = Collections.synchronizedMap(new HashMap<>());
  BukkitTask task = null;

  public void cancelLoading() {
    if (this.task != null) {
      this.task.cancel();
    }
  }

  public boolean stillLoading(World world) {
    return world == null
        ? this.stillLoading()
        : !worldMap.containsKey(world.getName()) && this.stillLoading();
  }

  public boolean stillLoading() {
    return this.task != null;
  }

  public WorldManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void removeWorldInfo(World world) {
    worldMap.remove(world.getName());
  }

  public void loadWorldChunksInfo(World world) {
    if (this.plugin.isEnabled()) {
      if (!this.plugin.getScanManager().isDisableWorldChunkCheckInfo()) {
        this.plugin.consoleMessage(
            "World (" + world.getName() + ") chunk information is being checked.");
      }

      WorldInfo worldInfo = this.getWorldInfo(world, null);
      if (this.plugin.isEnabled()) {
        worldMap.put(world.getName(), worldInfo);
        if (!this.plugin.getScanManager().isDisableWorldChunkCheckInfo()) {
          this.plugin.consoleMessage(
              "World ("
                  + world.getName()
                  + ") chunk information checked. "
                  + worldInfo.getTotalChunks());
        }

        this.task = null;
      }
    }
  }

  public void addChunk(Chunk chunk) {
    WorldInfo worldInfo = worldMap.get(chunk.getWorld().getName());
    int value = chunk.getX();
    int chunkZ = chunk.getZ();
    if (worldInfo == null) {
      World world = chunk.getWorld();
      worldInfo =
          new WorldInfo(
              world,
              new CMIRegion(
                  world.getSpawnLocation().getChunk().getX(),
                  world.getSpawnLocation().getChunk().getZ()));
      worldMap.put(world.getName(), worldInfo);
    }

    CMIRegion cmiRegion = getRegionCord(value, chunkZ);
    var list = worldInfo.getRegionPlaces(cmiRegion);
    if (list == null || list.isEmpty()) {
      list = new ArrayList<>(Collections.nCopies(1024, false));
    }

    int chunkIndex =
        CoordToChunkPlace(value - cmiRegion.getX() * 32, chunkZ - cmiRegion.getZ() * 32);
    list.set(chunkIndex, true);
    worldInfo.addTotalChunks();
    worldInfo.setRegionPlaces(cmiRegion, list);
  }

  public WorldInfo getWorldInfoInRange(World world, CMIVectorInt2D cmiVectorInt2D, int value) {
    return this.getWorldInfoInRange(
        world,
        new CMIRegion(cmiVectorInt2D.getX(), cmiVectorInt2D.getZ()),
        value == 0 ? null : value - 1);
  }

  public WorldInfo getWorldInfoInRange(World world, CMIRegion cmiRegion, Integer integer) {
    if (world == null) {
      return null;
    }

    WorldInfo worldInfo = null;
    if (cmiRegion == null || integer == null || integer > 1000) {
      worldInfo = this.getWorldInfoFromWorld(world);
      if (worldInfo != null) {
        worldInfo = worldInfo.clone();
      } else {
        this.loadWorldChunksInfo(world);
        worldInfo = this.getWorldInfoFromWorld(world);
        if (worldInfo != null) {
          worldInfo = worldInfo.clone();
        }
      }
    } else if (integer == -1) {
      worldInfo = this.getWorldInfo(world, cmiRegion, 5000);
    } else {
      worldInfo = this.getWorldInfo(world, cmiRegion, integer);
    }

    if (worldInfo == null) {
      worldInfo = this.fillEmpty(world, cmiRegion, integer);
    }

    return worldInfo;
  }

  public WorldInfo getWorldInfoFromWorld(World world) {
    return worldMap.get(world.getName());
  }

  public CMIRegion getNextInSpiral(long longValue) {
    int value = (int) Math.floor(Math.sqrt(longValue));
    int half = (int) Math.round(value / 2.0);
    double number = Math.pow(-1.0, value + 1);
    double leftTerm = value * (value + 1) - longValue;
    double rightTerm = Math.abs(value * (value + 1) - longValue);
    int spiralX = (int) (half * Math.pow(-1.0, value + 1) + number * (leftTerm - rightTerm) / 2.0);
    int spiralZ = (int) (half * Math.pow(-1.0, value) + number * (leftTerm + rightTerm) / 2.0);
    return new CMIRegion(spiralX, spiralZ);
  }

  public CMIRegion getNextChunkLocation(WorldInfo worldInfo, boolean flag) {
    CMIRegion cmiRegion = this.getNextInSpiral(worldInfo.getNextPlace());
    CMIRegion region =
        new CMIRegion(
            cmiRegion.getX() + worldInfo.getCenter().getX(),
            cmiRegion.getZ() + worldInfo.getCenter().getZ());
    CMIRegion regionCopy = getRegionCord(region);
    CMIRegion regionSpec =
        new CMIRegion(
            region.getX() - regionCopy.getX() * 32, region.getZ() - regionCopy.getZ() * 32);
    Boolean hasRegion =
        worldInfo.getRegionsChunk(
            regionCopy, CoordToChunkPlace(regionSpec.getX(), regionSpec.getZ()));
    if (hasRegion == null) {
      return null;
    } else {
      return (!flag || !hasRegion) && (flag || hasRegion) ? null : region;
    }
  }

  public Chunk getNextChunk(WorldInfo worldInfo, boolean flag) {
    CMIRegion cmiRegion = this.getNextChunkLocation(worldInfo, flag);
    return cmiRegion == null
        ? null
        : worldInfo.getWorld().getChunkAt(cmiRegion.getX(), cmiRegion.getZ());
  }

  public WorldInfo getWorldInfo(World world, CMIRegion cmiRegion, Integer integer) {
    Integer expectedCount;
    if (integer == null) {
      expectedCount = null;
    } else {
      expectedCount = integer;
      integer = integer + 1;
    }

    integer = expectedCount;
    new HashMap();
    HashMap<String, CMIRegion> hashMap;
    if (cmiRegion != null && integer != null) {
      hashMap = this.getRegionFiles(cmiRegion, integer);
    } else {
      hashMap = this.getRegionFiles(world);
    }

    if (world == null) {
      return null;
    }

    WorldInfo worldInfo = new WorldInfo(world, cmiRegion);
    WorldInfo loadedWorldInfo = worldMap.get(world.getName());
    if (loadedWorldInfo == null) {
      return null;
    }

    for (Entry entry : hashMap.entrySet()) {
      CMIRegion regionEntry = (CMIRegion) entry.getValue();
      if (regionEntry != null) {
        var list = loadedWorldInfo.getRegionPlaces(regionEntry);
        if (list != null) {
          ArrayList<Boolean> arrayList = new ArrayList(1024);

          for (int value = 0; value < 1024; value++) {
            arrayList.add(false);
          }

          int chunkCount = 0;

          for (int chunkIndex = 0; chunkIndex < 1024; chunkIndex++) {
            if ((Boolean) list.get(chunkIndex)) {
              CMIRegion chunkRegion = getRealChunkFromPlace(regionEntry, chunkIndex);
              if (inRange(cmiRegion, chunkRegion, integer)) {
                chunkCount++;
                arrayList.set(chunkIndex, true);
              }
            }
          }

          loadedWorldInfo.addTotalChunks(chunkCount);
          loadedWorldInfo.setRegionChunks(regionEntry, arrayList);
        }
      }
    }

    return loadedWorldInfo;
  }

  public WorldInfo fillEmpty(World world, CMIRegion cmiRegion, Integer integer) {
    new HashMap();
    var hashMap = this.getRegionFiles(cmiRegion, integer);
    if (world == null) {
      return null;
    }

    WorldInfo worldInfo = new WorldInfo(world, cmiRegion);
    worldInfo.setRange(integer);

    for (Entry entry : hashMap.entrySet()) {
      CMIRegion regionEntry = (CMIRegion) entry.getValue();
      if (regionEntry != null) {
        ArrayList<Boolean> arrayList = new ArrayList(1024);

        for (int value = 0; value < 1024; value++) {
          arrayList.add(false);
        }

        int chunkCount = 0;

        for (int chunkIndex = 0; chunkIndex < 1024; chunkIndex++) {
          CMIRegion chunkRegion = getRealChunkFromPlace(regionEntry, chunkIndex);
          if (inRange(cmiRegion, chunkRegion, integer)) {
            chunkCount++;
            arrayList.set(chunkIndex, true);
          }
        }

        worldInfo.addTotalChunks(chunkCount);
        worldInfo.setRegionChunks(regionEntry, arrayList);
      }
    }

    return worldInfo;
  }

  public WorldInfo getWorldInfo(World world, CMIRegion cmiRegion) {
    if (world == null) {
      return null;
    }

    WorldInfo worldInfo = new WorldInfo(world, cmiRegion);
    String string = "";
    if (new File(world.getWorldFolder(), "region").exists()) {
      string = "";
    } else if (new File(world.getWorldFolder(), "DIM-1").exists()) {
      string = "DIM-1" + File.separator;
    } else if (new File(world.getWorldFolder(), "DIM1").exists()) {
      string = "DIM1" + File.separator;
    }

    File file = new File(world.getWorldFolder(), string + "region");
    if (file.exists()) {
      File[] files;
      int value = (files = file.listFiles()).length;

      for (int fileIndex = 0; fileIndex < value; fileIndex++) {
        File regionFile = files[fileIndex];
        if (!this.plugin.isEnabled()) {
          return null;
        }

        Matcher matcher = this.regionPattern.matcher(regionFile.getName());
        if (matcher.matches()) {
          int regionX;
          int regionZ;
          try {
            regionX = Integer.parseInt(matcher.group(1));
            regionZ = Integer.parseInt(matcher.group(2));
          } catch (Exception exception) {
            continue;
          }

          CMIRegion loadedRegion = new CMIRegion(regionX, regionZ);
          ArrayList<Boolean> arrayList = new ArrayList(1024);

          for (int chunkIndex = 0; chunkIndex < 1024; chunkIndex++) {
            arrayList.add(false);
          }

          int chunkCount = 0;

          try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(regionFile, "r");

            for (int chunkScan = 0; chunkScan < 1024; chunkScan++) {
              if (randomAccessFile.readInt() != 0) {
                chunkCount++;
                arrayList.set(chunkScan, true);
              }
            }

            randomAccessFile.close();
          } catch (Exception exception2) {
            continue;
          }

          worldInfo.addTotalChunks(chunkCount);
          worldInfo.setRegionChunks(loadedRegion, arrayList);
        }
      }
    }

    return worldInfo;
  }

  private static boolean inRange(CMIRegion cmiRegion, CMIRegion other, Integer integer) {
    if (integer != null && cmiRegion != null && other != null) {
      int value = cmiRegion.getX() - other.getX();
      if (value < 0) {
        value = -value;
      }

      int dz = cmiRegion.getZ() - other.getZ();
      if (dz < 0) {
        dz = -dz;
      }

      return value <= integer && dz <= integer;
    } else {
      return true;
    }
  }

  public HashMap<String, CMIRegion> getRegionFiles(World world) {
    HashMap<String, CMIRegion> hashMap = new HashMap();
    if (world == null) {
      return hashMap;
    }

    String string = "";
    if (new File(world.getWorldFolder(), "region").exists()) {
      string = "";
    } else if (new File(world.getWorldFolder(), "DIM-1").exists()) {
      string = "DIM-1" + File.separator;
    } else if (new File(world.getWorldFolder(), "DIM1").exists()) {
      string = "DIM1" + File.separator;
    }

    File file = new File(world.getWorldFolder(), string + "region");
    if (file.exists()) {
      File[] files;
      int value = (files = file.listFiles()).length;

      for (int fileIndex = 0; fileIndex < value; fileIndex++) {
        File regionFile = files[fileIndex];
        Matcher matcher = this.regionPattern.matcher(regionFile.getName());
        if (matcher.matches()) {
          int regionX;
          int regionZ;
          try {
            regionX = Integer.parseInt(matcher.group(1));
            regionZ = Integer.parseInt(matcher.group(2));
          } catch (Exception exception) {
            continue;
          }

          hashMap.put(
              (regionX >> 5) + ":" + (regionZ >> 5), new CMIRegion(regionX >> 5, regionZ >> 5));
        }
      }
    }

    return hashMap;
  }

  public HashMap<String, CMIRegion> getRegionFiles(CMIRegion cmiRegion, Integer integer) {
    HashMap<String, CMIRegion> hashMap = new HashMap();
    if (cmiRegion == null) {
      return hashMap;
    }

    if (integer == null) {
      integer = 1024;
    }

    int value = cmiRegion.getX();
    int centerZ = cmiRegion.getZ();
    hashMap.put((value >> 5) + ":" + (centerZ >> 5), new CMIRegion(value >> 5, centerZ >> 5));

    for (int loopX = value - integer; loopX <= value + integer; loopX += 32) {
      for (int loopZ = centerZ - integer; loopZ <= centerZ + integer; loopZ += 32) {
        hashMap.put((loopX >> 5) + ":" + (loopZ >> 5), new CMIRegion(loopX >> 5, loopZ >> 5));
        if (loopZ + 32 > centerZ + integer && loopZ < centerZ + integer) {
          loopZ = centerZ + integer - 32;
        }
      }

      if (loopX + 32 > value + integer && loopX < value + integer) {
        loopX = value + integer - 32;
      }
    }

    return hashMap;
  }

  private static CMIRegion getRegionCord(CMIRegion cmiRegion) {
    return getRegionCord(cmiRegion.getX(), cmiRegion.getZ());
  }

  private static CMIRegion getRegionCord(int value, int z) {
    return new CMIRegion(value >> 5, z >> 5);
  }

  private static CMIRegion getRealChunkFromPlace(CMIRegion cmiRegion, int value) {
    CMIRegion region = getCordFromPlace(value);
    return new CMIRegion(
        cmiRegion.getX() * 32 + region.getX(), cmiRegion.getZ() * 32 + region.getZ());
  }

  private static CMIRegion getCordFromPlace(int value) {
    return new CMIRegion(value % 32, value / 32);
  }

  private static int CoordToChunkPlace(int value, int z) {
    return value + z * 32;
  }
}
