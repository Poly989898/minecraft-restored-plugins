package io.lumine.mythic.core.volatilecode.v26_2.ai.pathfinding;

import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.pathfinder.Path;

final class PendingPath {
   final Path stub;
   final CompletableFuture<Path> future = new CompletableFuture<>();
   volatile Path result;
   volatile boolean done;
   volatile boolean evicted;
   volatile boolean awaited;
   int waited;

   PendingPath(Path stub) {
      this.stub = stub;
   }
}
