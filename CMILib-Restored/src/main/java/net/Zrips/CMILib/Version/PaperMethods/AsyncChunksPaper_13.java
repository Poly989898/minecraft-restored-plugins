package net.Zrips.CMILib.Version.PaperMethods;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.World;

public class AsyncChunksPaper_13 implements AsyncChunks {
  @Override
  public CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen) {
    return world == null
        ? CompletableFuture.completedFuture(null)
        : world.getChunkAtAsync(x, z, gen);
  }
}
