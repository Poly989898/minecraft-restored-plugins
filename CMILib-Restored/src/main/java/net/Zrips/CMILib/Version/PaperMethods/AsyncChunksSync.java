package net.Zrips.CMILib.Version.PaperMethods;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.World;

public class AsyncChunksSync implements AsyncChunks {
  @Override
  public CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen) {
    return !gen && !PaperLib.isChunkGenerated(world, x, z)
        ? CompletableFuture.completedFuture(null)
        : CompletableFuture.completedFuture(world.getChunkAt(x, z));
  }
}
