package io.lumine.mythic.core.volatilecode.v26_1.ai.pathfinding;

import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;

public final class AsyncPathFinder extends PathFinder {
   private final AsyncNavigationState state;
   private final Workers.Kind kind;
   private int maxVisitedNodes;
   private BooleanSupplier captureDebug = () -> false;

   AsyncPathFinder(AsyncNavigationState state, NodeEvaluator evaluator, int maxVisitedNodes, Workers.Kind kind) {
      super(evaluator, maxVisitedNodes);
      this.state = state;
      this.kind = kind;
      this.maxVisitedNodes = maxVisitedNodes;
   }

   public void setMaxVisitedNodes(int maxVisitedNodes) {
      super.setMaxVisitedNodes(maxVisitedNodes);
      this.maxVisitedNodes = maxVisitedNodes;
   }

   public void setCaptureDebug(BooleanSupplier captureDebug) {
      super.setCaptureDebug(captureDebug);
      this.captureDebug = captureDebug;
   }

   public Path findPath(PathNavigationRegion region, Mob mob, Set<BlockPos> targets, float maxPathLength, int reachRange, float maxVisitedNodesMultiplier) {
      if (AsyncPathfinding.enabled() && !this.captureDebug.getAsBoolean()) {
         int radius = (int)maxPathLength + 16;
         BlockPos from = mob.blockPosition();
         AsyncRegion asyncRegion = new AsyncRegion(mob.level(), from.offset(-radius, -radius, -radius), from.offset(radius, radius, radius));
         Path stub = new Path(new ArrayList<>(List.of(new Node(from.getX(), from.getY(), from.getZ()))), targets.iterator().next(), false);
         PendingPath job = new PendingPath(stub);
         NodeEvaluator source = this.nodeEvaluator;
         boolean passDoors = source.canPassDoors();
         boolean openDoors = source.canOpenDoors();
         boolean canFloat = source.canFloat();
         boolean overFences = source.canWalkOverFences();
         int max = this.maxVisitedNodes;
         Runnable task = () -> {
            try {
               if (!job.evicted || job.awaited) {
                  PathFinder finder = Workers.get(this.kind);
                  finder.setMaxVisitedNodes(max);
                  NodeEvaluator evaluator = finder.nodeEvaluator;
                  evaluator.setCanPassDoors(passDoors);
                  evaluator.setCanOpenDoors(openDoors);
                  evaluator.setCanFloat(canFloat);
                  evaluator.setCanWalkOverFences(overFences);
                  job.result = finder.findPath(asyncRegion, mob, targets, maxPathLength, reachRange, maxVisitedNodesMultiplier);
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
         } catch (RejectedExecutionException e) {
            AsyncPathfinding.SYNC_FALLBACK.increment();
            return super.findPath(region, mob, targets, maxPathLength, reachRange, maxVisitedNodesMultiplier);
         }

         this.state.add(job);
         return stub;
      } else {
         AsyncPathfinding.SYNC_FALLBACK.increment();
         return super.findPath(region, mob, targets, maxPathLength, reachRange, maxVisitedNodesMultiplier);
      }
   }
}
