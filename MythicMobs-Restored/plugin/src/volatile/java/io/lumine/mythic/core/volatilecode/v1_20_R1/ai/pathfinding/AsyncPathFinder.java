package io.lumine.mythic.core.volatilecode.v1_20_R1.ai.pathfinding;

import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.ChunkCache;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderAbstract;

public final class AsyncPathFinder extends Pathfinder {
   private final AsyncNavigationState state;
   private final Workers.Kind kind;
   private final int maxVisitedNodes;

   AsyncPathFinder(AsyncNavigationState state, PathfinderAbstract evaluator, int maxVisitedNodes, Workers.Kind kind) {
      super(evaluator, maxVisitedNodes);
      this.state = state;
      this.kind = kind;
      this.maxVisitedNodes = maxVisitedNodes;
   }

   public PathEntity a(
      ChunkCache region, EntityInsentient mob, Set<BlockPosition> targets, float maxPathLength, int reachRange, float maxVisitedNodesMultiplier
   ) {
      if (!AsyncPathfinding.enabled()) {
         AsyncPathfinding.SYNC_FALLBACK.increment();
         return super.a(region, mob, targets, maxPathLength, reachRange, maxVisitedNodesMultiplier);
      } else {
         int radius = (int)maxPathLength + 16;
         BlockPosition from = mob.di();
         AsyncRegion asyncRegion = new AsyncRegion(mob.dI(), from.b(-radius, -radius, -radius), from.b(radius, radius, radius));
         PathEntity stub = new PathEntity(new ArrayList<>(List.of(new PathPoint(from.u(), from.v(), from.w()))), targets.iterator().next(), false);
         PendingPath job = new PendingPath(stub);
         PathfinderAbstract source = this.d;
         boolean passDoors = source.d();
         boolean openDoors = source.e();
         boolean canFloat = source.f();
         boolean overFences = source.g();
         int max = this.maxVisitedNodes;
         Runnable task = () -> {
            try {
               if (!job.evicted || job.awaited) {
                  Pathfinder finder = Workers.get(this.kind, max);
                  PathfinderAbstract evaluator = finder.d;
                  evaluator.a(passDoors);
                  evaluator.b(openDoors);
                  evaluator.c(canFloat);
                  evaluator.d(overFences);
                  job.result = finder.a(asyncRegion, mob, targets, maxPathLength, reachRange, maxVisitedNodesMultiplier);
                  AsyncPathfinding.COMPLETED.increment();
                  return;
               }

               AsyncPathfinding.SKIPPED_STALE.increment();
            } catch (Throwable var18x) {
               AsyncPathfinding.FAILED.increment();
               Workers.drop(this.kind);
               MythicLogger.handleMinorError(var18x);
               job.future.completeExceptionally(var18x);
               return;
            } finally {
               job.done = true;
               job.future.complete(job.result);
            }
         };

         try {
            AsyncPathfinding.submit(task);
            AsyncPathfinding.SUBMITTED.increment();
         } catch (RejectedExecutionException var20) {
            AsyncPathfinding.SYNC_FALLBACK.increment();
            return super.a(region, mob, targets, maxPathLength, reachRange, maxVisitedNodesMultiplier);
         }

         this.state.add(job);
         return stub;
      }
   }
}
