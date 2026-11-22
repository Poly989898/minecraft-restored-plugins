package com.Zrips.CMI.Modules.Scan;

import com.Zrips.CMI.CMI;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.BitSet;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Container.CMIVectorInt2D;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.World;

public class CMIWorldChunkManager {
  private CMI plugin;
  private static final Pattern regionPattern = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.mca");
  private boolean disableWorldChunkCheckInfo = true;
  private double scanSoftCap = 19.0;
  private int scanDefaultSpeed = 15;
  private int defaultRange = 3;
  private boolean purge = false;
  private boolean logIntoFile = false;

  public CMIWorldChunkManager(CMI cmi) {
    this.plugin = cmi;
  }

  public boolean isDisableWorldChunkCheckInfo() {
    return this.disableWorldChunkCheckInfo;
  }

  public double getScanSoftCap() {
    return this.scanSoftCap;
  }

  public int getScanDefaultSpeed() {
    return this.scanDefaultSpeed;
  }

  public int getDefaultRange() {
    return this.defaultRange;
  }

  public boolean isPurge() {
    return this.purge;
  }

  public boolean isLogIntoFile() {
    return this.logIntoFile;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "scan.DisableWorldChunkCheckInfo",
        new String[] {"Can disable messages outputed durring start for world chunk checks"});
    this.disableWorldChunkCheckInfo =
        configReader.get("Optimizations.DisableWorldChunkCheckInfo", false);
    configReader.addComment(
        "scan.SoftCap", new String[] {"TPS cap from which to start adjusting scan speed"});
    this.scanSoftCap = configReader.get("scan.SoftCap", 19.0);
    if (this.scanSoftCap < 0.0) {
      this.scanSoftCap = 1.0;
    }

    if (this.scanSoftCap > 20.0) {
      this.scanSoftCap = 20.0;
    }

    configReader.addComment(
        "scan.DefaultSpeed", new String[] {"Starting speed when scanning. Range from 1 to 30"});
    this.scanDefaultSpeed = configReader.get("scan.DefaultSpeed", 15);
    if (this.scanDefaultSpeed < 0) {
      this.scanDefaultSpeed = 1;
    }

    if (this.scanDefaultSpeed > 30) {
      this.scanDefaultSpeed = 30;
    }

    configReader.addComment(
        "scan.DefaultRange",
        new String[] {
          "Range in chunks. 2 is 9 chunks (3x3), 1 is the one you stand in and 0 is global scan"
        });
    this.defaultRange = configReader.get("scan.DefaultRange", 2);
    configReader.addComment(
        "scan.LogIntoFile",
        new String[] {
          "When enabled scan command output will be printed into file for option to go over it"
              + " later on"
        });
    this.logIntoFile = configReader.get("scan.LogIntoFile", false);
  }

  private CompletableFuture<CMIWorldChunks> loadWorldChunksInfo(
      World world, CMIVectorInt2D cmiVectorInt2D, int value) {
    if (!CMI.getInstance().isEnabled()) {
      return CompletableFuture.completedFuture(null);
    }

    if (!this.isDisableWorldChunkCheckInfo()) {
      CMI.getInstance()
          .consoleMessage("World (" + world.getName() + ") chunk information is being checked.");
    }

    return this.loadWorldChunksInRangeAsync(world, cmiVectorInt2D, value)
        .thenApply(
            cmiWorldChunks -> {
              if (cmiWorldChunks != null && CMI.getInstance().isEnabled()) {
                if (!this.isDisableWorldChunkCheckInfo()) {
                  CMI.getInstance()
                      .consoleMessage(
                          "World ("
                              + world.getName()
                              + ") chunk information checked. "
                              + cmiWorldChunks.getTotalChunks());
                }

                return (CMIWorldChunks) cmiWorldChunks;
              } else {
                return null;
              }
            });
  }

  public CompletableFuture<CMIWorldChunksRanged> getWorldChunkInfoInRange(
      World world, CMIVectorInt2D cmiVectorInt2D) {
    return this.getWorldChunkInfoInRangeAsync(world, cmiVectorInt2D, 0);
  }

  public CompletableFuture<CMIWorldChunksRanged> getWorldChunkInfoInRangeAsync(
      World world, CMIVectorInt2D cmiVectorInt2D, int value) {
    return this.loadWorldChunksInfo(world, cmiVectorInt2D, value)
        .thenCompose(
            cmiWorldChunks -> {
              CMIWorldChunksRanged cmiWorldChunksRanged =
                  new CMIWorldChunksRanged(world, cmiVectorInt2D, value);
              cmiWorldChunksRanged.setRegion(cmiWorldChunks.getRegionChunks());
              cmiWorldChunksRanged.setTotalChunks(cmiWorldChunks.getTotalChunks());
              return CompletableFuture.completedFuture(cmiWorldChunksRanged);
            });
  }

  public CompletableFuture<CMIWorldChunks> loadWorldChunksInRangeAsync(
      World world, CMIVectorInt2D cmiVectorInt2D, int value) {
    return CompletableFuture.supplyAsync(
        () -> {
          if (world != null && CMI.getInstance().isEnabled()) {
            CMIWorldChunks cmiWorldChunks = new CMIWorldChunks(world);
            File file = world.getWorldFolder();
            String[] strings = new String[] {"region", "DIM-1/region", "DIM1/region"};
            File regionDir = null;
            String[] candidateDirs = strings;
            int candidateDirCount = strings.length;

            for (int dirIndex = 0; dirIndex < candidateDirCount; dirIndex++) {
              String string = candidateDirs[dirIndex];
              File candidateDir = new File(file, string);
              if (candidateDir.exists()) {
                regionDir = candidateDir;
                break;
              }
            }

            if (regionDir != null && regionDir.exists()) {
              if (cmiVectorInt2D != null && value > 0) {
                int rangeOffset = value - 1;
                int minX = cmiVectorInt2D.getX() - rangeOffset;
                int maxX = cmiVectorInt2D.getX() + rangeOffset;
                int minZ = cmiVectorInt2D.getZ() - rangeOffset;
                int maxZ = cmiVectorInt2D.getZ() + rangeOffset;
                int minRegionX = minX >> 5;
                int maxRegionX = maxX >> 5;
                int minRegionZ = minZ >> 5;
                int maxRegionZ = maxZ >> 5;

                for (int regionX = minRegionX; regionX <= maxRegionX; regionX++) {
                  for (int regionZ = minRegionZ; regionZ <= maxRegionZ; regionZ++) {
                    File regionFile = new File(regionDir, "r." + regionX + "." + regionZ + ".mca");
                    if (regionFile.exists()
                        && regionPattern.matcher(regionFile.getName()).matches()) {
                      BitSet bitSet = new BitSet(1024);
                      int chunkCount = 0;

                      try {
                        Throwable throwable = null;
                        Object object = null;

                        try {
                          RandomAccessFile randomAccessFile = new RandomAccessFile(regionFile, "r");

                          try {
                            for (int slotIndex = 0; slotIndex < 1024; slotIndex++) {
                              int localX = slotIndex % 32;
                              int localZ = slotIndex / 32;
                              int chunkX = (regionX << 5) + localX;
                              int chunkZ = (regionZ << 5) + localZ;
                              if (chunkX >= minX
                                  && chunkX <= maxX
                                  && chunkZ >= minZ
                                  && chunkZ <= maxZ
                                  && randomAccessFile.readInt() != 0) {
                                bitSet.set(slotIndex);
                                chunkCount++;
                              }
                            }
                          } finally {
                            if (randomAccessFile != null) {
                              randomAccessFile.close();
                            }
                          }
                        } catch (Throwable throwable2) {
                          if (throwable == null) {
                            throwable = throwable2;
                          } else if (throwable != throwable2) {
                            throwable.addSuppressed(throwable2);
                          }

                          throw sneakyThrow(throwable);
                        }
                      } catch (Exception exception) {
                        continue;
                      }

                      if (chunkCount > 0) {
                        CMIVectorInt2D regionCoords = new CMIVectorInt2D(regionX, regionZ);
                        cmiWorldChunks.addTotalChunks(chunkCount);
                        cmiWorldChunks.setRegionChunks(regionCoords, bitSet);
                      }
                    }
                  }
                }
              } else {
                File[] files;
                int fileCount = (files = regionDir.listFiles()).length;

                for (int fileIndex = 0; fileIndex < fileCount; fileIndex++) {
                  File regionFile = files[fileIndex];
                  if (!CMI.getInstance().isEnabled()) {
                    return null;
                  }

                  Matcher matcher = regionPattern.matcher(regionFile.getName());
                  if (matcher.matches()) {
                    int fileRegionX;
                    int fileRegionZ;
                    try {
                      fileRegionX = Integer.parseInt(matcher.group(1));
                      fileRegionZ = Integer.parseInt(matcher.group(2));
                    } catch (Exception exception2) {
                      continue;
                    }

                    BitSet regionBits = new BitSet(1024);
                    int chunkCount = 0;

                    try {
                      Throwable primaryError = null;
                      Object unusedResource = null;

                      try {
                        RandomAccessFile regionReader = new RandomAccessFile(regionFile, "r");

                        try {
                          for (int bitIndex = 0; bitIndex < 1024; bitIndex++) {
                            if (regionReader.readInt() != 0) {
                              regionBits.set(bitIndex);
                              chunkCount++;
                            }
                          }
                        } finally {
                          if (regionReader != null) {
                            regionReader.close();
                          }
                        }
                      } catch (Throwable throwable4) {
                        if (primaryError == null) {
                          primaryError = throwable4;
                        } else if (primaryError != throwable4) {
                          primaryError.addSuppressed(throwable4);
                        }

                        throw sneakyThrow(primaryError);
                      }
                    } catch (Exception exception3) {
                      continue;
                    }

                    CMIVectorInt2D regionCoords = new CMIVectorInt2D(fileRegionX, fileRegionZ);
                    cmiWorldChunks.addTotalChunks(chunkCount);
                    cmiWorldChunks.setRegionChunks(regionCoords, regionBits);
                  }
                }
              }

              return cmiWorldChunks;
            } else {
              return cmiWorldChunks;
            }
          } else {
            return null;
          }
        });
  }

  public static HashMap<Long, CMIVectorInt2D> getRegionFiles(
      CMIVectorInt2D cmiVectorInt2D, int value) {
    HashMap<Long, CMIVectorInt2D> hashMap = new HashMap();
    if (cmiVectorInt2D == null) {
      return hashMap;
    }

    if (value <= 0) {
      value = 1024;
    }

    int centerRegionX = cmiVectorInt2D.getX() >> 5;
    int centerRegionZ = cmiVectorInt2D.getZ() >> 5;
    int regionRadius = value + 31 >> 5;

    for (int regionX = centerRegionX - regionRadius;
        regionX <= centerRegionX + regionRadius;
        regionX++) {
      for (int regionZ = centerRegionZ - regionRadius;
          regionZ <= centerRegionZ + regionRadius;
          regionZ++) {
        hashMap.put(
            CMIWorldChunks.packRegionCoords(regionX, regionZ),
            new CMIVectorInt2D(regionX, regionZ));
      }
    }

    return hashMap;
  }

  /**
   * Rethrows the object unchanged (the original bytecode throws Throwable without a declaration).
   */
  @SuppressWarnings("unchecked")
  private static <T extends Throwable> RuntimeException sneakyThrow(Throwable throwable) throws T {
    throw (T) throwable;
  }
}
