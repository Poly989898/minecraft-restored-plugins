package net.Zrips.CMILib.Version.PaperMethods;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.World;

public interface AsyncChunks {
  CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen);
}
