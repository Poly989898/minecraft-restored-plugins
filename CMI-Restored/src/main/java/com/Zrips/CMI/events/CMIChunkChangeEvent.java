package com.Zrips.CMI.events;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class CMIChunkChangeEvent extends CMIPlayerCancellableEvent {
  private Chunk oldChunk;
  private Chunk newChunk;

  public CMIChunkChangeEvent(Player player, Chunk chunk, Chunk toChunk) {
    super(player);
    this.oldChunk = chunk;
    this.newChunk = toChunk;
  }

  public Chunk getOldChunk() {
    return this.oldChunk;
  }

  public Chunk getNewChunk() {
    return this.newChunk;
  }
}
