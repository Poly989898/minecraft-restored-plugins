package io.lumine.mythic.core.volatilecode.v1_21_R1.ai.pathfinding;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.phys.Vec3D;

public final class AsyncFlyingPathNavigation extends NavigationFlying implements AsyncNavigation {
   private AsyncNavigationState state;
   private final boolean meg;

   public AsyncFlyingPathNavigation(EntityInsentient mob, World level, boolean meg) {
      super(mob, level);
      this.meg = meg;
   }

   protected Pathfinder a(int maxVisitedNodes) {
      super.a(maxVisitedNodes);
      this.state = new AsyncNavigationState();
      return new AsyncPathFinder(this.state, this.o, maxVisitedNodes, Workers.Kind.FLY);
   }

   public void c() {
      PendingPath job = this.state.find(this.c);
      if (job != null) {
         if (!job.done) {
            if (AsyncPathfinding.enabled() && ++job.waited <= 40) {
               this.e++;
               return;
            }

            this.c = null;
            this.state.remove(job);
         } else {
            this.state.remove(job);
            this.a(job.result, this.d);
         }
      }

      super.c();
   }

   public boolean a(PathEntity newPath, double speedModifier) {
      PendingPath job = this.state.find(newPath);
      if (job != null) {
         if (!job.done) {
            this.c = newPath;
            this.d = speedModifier;
            this.f = this.e;
            this.g = this.b();
            return true;
         }

         newPath = job.result;
         this.state.remove(job);
      }

      if (this.meg && newPath != null && MegSupport.overrideMoveTo()) {
         MegSupport.snapToClosestNode(newPath, this.c, this.a);
      }

      return super.a(newPath, speedModifier);
   }

   protected void k() {
      if (this.state.find(this.c) == null) {
         if (!this.meg) {
            super.k();
         } else {
            Vec3D mobPos = this.b();
            this.l = this.a.dj() > 0.75F ? this.a.dj() / 2.0F : 0.75F - this.a.dj() / 2.0F;
            if (MegSupport.shouldAdvance(this.c, this.a, mobPos, this.l, this.b(this.c.h().l), (x$0, x$1) -> this.a(x$0, x$1))) {
               this.c.a();
            }

            this.b(mobPos);
         }
      }
   }

   public boolean b(BlockPosition pos) {
      return this.state.find(this.c) == null && super.b(pos);
   }

   public CompletableFuture<PathEntity> getPathFuture(PathEntity path) {
      return this.state.future(path);
   }
}
