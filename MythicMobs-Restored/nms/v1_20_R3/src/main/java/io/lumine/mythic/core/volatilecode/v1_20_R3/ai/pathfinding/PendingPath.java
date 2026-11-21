package io.lumine.mythic.core.volatilecode.v1_20_R3.ai.pathfinding;

import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.pathfinder.PathEntity;

final class PendingPath {
   final PathEntity stub;
   final CompletableFuture<PathEntity> future = new CompletableFuture<>();
   volatile PathEntity result;
   volatile boolean done;
   volatile boolean evicted;
   volatile boolean awaited;
   int waited;

   PendingPath(PathEntity stub) {
      this.stub = stub;
   }
}
