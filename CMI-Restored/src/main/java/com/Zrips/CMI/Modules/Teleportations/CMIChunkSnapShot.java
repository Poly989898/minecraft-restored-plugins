package com.Zrips.CMI.Modules.Teleportations;

import net.Zrips.CMILib.Container.CMIWorld;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

public class CMIChunkSnapShot {
  private ChunkSnapshot snap;
  private int maxY = 128;
  private int minY = 0;
  private World world;

  public CMIChunkSnapShot(World snapshotWorld) {
    this.world = snapshotWorld;
    this.maxY = CMIWorld.getMaxHeight(snapshotWorld);
    this.minY = CMIWorld.getMinHeight(snapshotWorld);
  }

  public CMIChunkSnapShot setSnapshot(ChunkSnapshot chunkSnapshot) {
    this.snap = chunkSnapshot;
    return this;
  }

  public ChunkSnapshot getSnapshot() {
    return this.snap;
  }

  public int getMaxY() {
    return this.maxY;
  }

  public CMIChunkSnapShot setMaxY(int value) {
    this.maxY = value;
    return this;
  }

  public int getMinY() {
    return this.minY;
  }

  public CMIChunkSnapShot setMinY(int value) {
    this.minY = value;
    return this;
  }

  public World getWorld() {
    return this.world;
  }
}
