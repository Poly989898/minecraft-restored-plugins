package com.Zrips.CMI.Modules.Worlds;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.Zrips.CMILib.Container.CMIBiome;
import net.Zrips.CMILib.Container.CMIBiomeType;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

public class CMIChunk {
  private Integer x;
  private Integer z;
  private World world;
  private boolean generated = false;
  private Color color = null;
  private Color mostCommonColor = null;
  private List<CMIBiome> biomes = new ArrayList<>();

  public CMIChunk(Chunk chunk) {
    this(chunk.getWorld(), chunk.getX(), chunk.getZ());
    this.setGenerated(true);
    this.recheckBiomes(chunk);
  }

  public CMIChunk(World chunkWorld, int value, int chunkZ) {
    this.x = value;
    this.z = chunkZ;
    this.world = chunkWorld;
  }

  public int getRelativeX() {
    return this.x - (this.x >> 5) * 32;
  }

  public int getRelativeZ() {
    return this.z - (this.z >> 5) * 32;
  }

  public Integer getPlace() {
    return this.getRelativeX() + this.getRelativeZ() * 32;
  }

  private static Integer convertBiomePlace(int value, int zIndex) {
    return value + zIndex * 16;
  }

  public Integer getX() {
    return this.x;
  }

  public Integer getZ() {
    return this.z;
  }

  public CMIChunk recheckBiomes(Chunk chunk) {
    this.biomes.clear();
    ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot(false, true, false);
    if (chunkSnapshot != null) {
      for (byte b = 0; b < 16; b += 15) {
        for (byte blockZ = 0; blockZ < 16; blockZ += 15) {
          this.biomes.add(CMIBiome.get(chunkSnapshot.getBiome(b, blockZ).toString()));
        }
      }
    }

    return this;
  }

  public CMIChunk recheckBiomes() {
    this.biomes.clear();
    ChunkSnapshot chunkSnapshot = this.getEmptyChunkSnapshot();
    if (chunkSnapshot != null) {
      for (byte b = 4; b < 16; b += 8) {
        for (byte blockZ = 4; blockZ < 16; blockZ += 8) {
          this.biomes.add(CMIBiome.get(chunkSnapshot.getBiome(b, blockZ).toString()));
        }
      }
    }

    return this;
  }

  public World getWorld() {
    return this.world;
  }

  public ChunkSnapshot getEmptyChunkSnapshot() {
    return this.world.getEmptyChunkSnapshot(this.x, this.z, true, false);
  }

  public boolean isGenerated() {
    return this.generated;
  }

  public CMIChunk setGenerated(boolean flag) {
    this.generated = flag;
    return this;
  }

  public Chunk getChunk() {
    return this.getWorld().getChunkAt(this.x, this.z);
  }

  public List<CMIBiome> getBiomes() {
    return this.biomes;
  }

  public String getBiomeColorCodes() {
    String string = "";
    String suffix = "";

    for (CMIBiome cmiBiome : this.biomes) {
      string = string + cmiBiome.getId();
    }

    for (int value = 0; value < this.biomes.size(); value++) {
      if (string.endsWith(suffix)
          && string.substring(0, string.length() - suffix.length()).endsWith(suffix)) {
        string = string.substring(0, string.length() - suffix.length());
      }
    }

    return string;
  }

  public Color getUnbluredColor() {
    if (this.mostCommonColor == null) {
      HashMap<CMIBiome, Integer> hashMap = new HashMap();
      int value = 0;
      CMIBiome cmiBiome = null;

      for (CMIBiome candidateBiome : this.biomes) {
        if (hashMap.containsKey(candidateBiome)) {
          hashMap.put(candidateBiome, (Integer) hashMap.get(candidateBiome) + 1);
          if (value < (Integer) hashMap.get(candidateBiome)) {
            value = (Integer) hashMap.get(candidateBiome);
            cmiBiome = candidateBiome;
          }
        } else {
          hashMap.put(candidateBiome, 1);
          if (value < (Integer) hashMap.get(candidateBiome)) {
            value = (Integer) hashMap.get(candidateBiome);
            cmiBiome = candidateBiome;
          }
        }
      }

      if (cmiBiome == null) {
        return CMIBiomeType.PLAINS.getColor();
      }

      this.mostCommonColor = cmiBiome.getColor();
    }

    return this.mostCommonColor;
  }

  private static Color blend(Color firstColor, Color secondColor) {
    double number = firstColor.getAlpha() + secondColor.getAlpha();
    double firstWeight = firstColor.getAlpha() / number;
    double secondWeight = secondColor.getAlpha() / number;
    double blendedRed = firstWeight * firstColor.getRed() + secondWeight * secondColor.getRed();
    double blendedGreen =
        firstWeight * firstColor.getGreen() + secondWeight * secondColor.getGreen();
    double blendedBlue = firstWeight * firstColor.getBlue() + secondWeight * secondColor.getBlue();
    double blendedAlpha = Math.max(firstColor.getAlpha(), secondColor.getAlpha());
    return new Color((int) blendedRed, (int) blendedGreen, (int) blendedBlue, (int) blendedAlpha);
  }

  public void setBiomes(List<CMIBiome> list) {
    this.biomes = new ArrayList<>(list);
  }
}
