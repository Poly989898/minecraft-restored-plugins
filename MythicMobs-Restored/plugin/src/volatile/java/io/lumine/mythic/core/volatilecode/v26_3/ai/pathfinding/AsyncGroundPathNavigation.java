package io.lumine.mythic.core.volatilecode.v26_3.ai.pathfinding;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public final class AsyncGroundPathNavigation extends GroundPathNavigation implements AsyncNavigation {
   private AsyncNavigationState state;
   private final boolean meg;

   public AsyncGroundPathNavigation(Mob mob, Level level, boolean meg) {
      super(mob, level);
      this.meg = meg;
   }

   protected PathFinder createPathFinder(int maxVisitedNodes) {
      super.createPathFinder(maxVisitedNodes);
      this.state = new AsyncNavigationState();
      return new AsyncPathFinder(this.state, this.nodeEvaluator, maxVisitedNodes, Workers.Kind.WALK);
   }

   public void tick() {
      PendingPath job = this.state.find(this.path);
      if (job != null) {
         if (!job.done) {
            if (AsyncPathfinding.enabled() && ++job.waited <= 40) {
               this.tick++;
               return;
            }

            this.path = null;
            this.state.remove(job);
         } else {
            this.state.remove(job);
            this.moveTo(job.result, this.speedModifier);
         }
      }

      super.tick();
   }

   public boolean moveTo(Path newPath, double speedModifier) {
      PendingPath job = this.state.find(newPath);
      if (job != null) {
         if (!job.done) {
            this.path = newPath;
            this.speedModifier = speedModifier;
            this.lastStuckCheck = this.tick;
            this.lastStuckCheckPos = this.getTempMobPos();
            return true;
         }

         newPath = job.result;
         this.state.remove(job);
      }

      if (this.meg && newPath != null && MegSupport.overrideMoveTo()) {
         MegSupport.snapToClosestNode(newPath, this.path, this.mob);
      }

      return super.moveTo(newPath, speedModifier);
   }

   protected void followThePath() {
      if (this.state.find(this.path) == null) {
         if (!this.meg) {
            super.followThePath();
         } else {
            Vec3 mobPos = this.getTempMobPos();
            this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
            if (MegSupport.shouldAdvance(
               this.path,
               this.mob,
               mobPos,
               this.maxDistanceToWaypoint,
               this.canCutCorner(this.path.getNextNode().type),
               (x$0, x$1) -> this.canMoveDirectly(x$0, x$1)
            )) {
               this.path.advance();
            }

            this.doStuckDetection(mobPos);
         }
      }
   }

   public boolean shouldRecomputePath(BlockPos pos) {
      return this.state.find(this.path) == null && super.shouldRecomputePath(pos);
   }

   public CompletableFuture<Path> getPathFuture(Path path) {
      return this.state.future(path);
   }
}
