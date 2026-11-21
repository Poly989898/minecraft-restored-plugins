package io.lumine.mythic.core.volatilecode.v1_21_R6.ai.pathfinding;

import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.pathfinder.PathEntity;

public final class AsyncNavigationState {
   private final PendingPath[] pending = new PendingPath[4];
   private int next;

   void add(PendingPath job) {
      PendingPath old = this.pending[this.next];
      if (old != null) {
         old.evicted = true;
      }

      this.pending[this.next] = job;
      this.next = this.next + 1 & 3;
   }

   PendingPath find(PathEntity path) {
      if (path == null) {
         return null;
      } else {
         for (PendingPath job : this.pending) {
            if (job != null && job.stub == path) {
               return job;
            }
         }

         return null;
      }
   }

   CompletableFuture<PathEntity> future(PathEntity path) {
      PendingPath job = this.find(path);
      if (job == null) {
         return CompletableFuture.completedFuture(path);
      } else {
         job.awaited = true;
         return job.future;
      }
   }

   void remove(PendingPath job) {
      for (int i = 0; i < this.pending.length; i++) {
         if (this.pending[i] == job) {
            this.pending[i] = null;
         }
      }
   }
}
