package net.Zrips.CMILib.Version.PaperMethods;

import net.Zrips.CMILib.Container.CMIWorld;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

public class CMIChunkSnapShot {
  private ChunkSnapshot snap;
  private int maxY = 128;
  private int minY = 0;
  private World world;

  public CMIChunkSnapShot(World world) {
    this.world = world;
    this.maxY = CMIWorld.getMaxHeight(world);
    this.minY = CMIWorld.getMinHeight(world);
  }

  public CMIChunkSnapShot setSnapshot(ChunkSnapshot snap) {
    this.snap = snap;
    return this;
  }

  public ChunkSnapshot getSnapshot() {
    return this.snap;
  }

  public int getMaxY() {
    return this.maxY;
  }

  public CMIChunkSnapShot setMaxY(int maxY) {
    this.maxY = maxY;
    return this;
  }

  public int getMinY() {
    return this.minY;
  }

  public CMIChunkSnapShot setMinY(int minY) {
    this.minY = minY;
    return this;
  }

  public World getWorld() {
    return this.world;
  }
}
