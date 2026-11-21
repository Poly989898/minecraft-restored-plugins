package com.ticxo.modelengine.v1_20_R2.entity.navigation;

import com.ticxo.modelengine.api.ModelEngineAPI;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Nullable;

public class FlyingNavigationWrapper extends NavigationFlying {
   public FlyingNavigationWrapper(EntityInsentient mob, NavigationFlying oldNav) {
      super(mob, mob.dL());
      this.a(oldNav.p());
      this.b(oldNav.e());
      this.c(oldNav.d());
   }

   public boolean a(@Nullable PathEntity newPath, double speed) {
      if (!ModelEngineAPI.getConfigCache().isOverrideMoveTo()) {
         return super.a(newPath, speed);
      }

      if (newPath != null && !newPath.a(this.c)) {
         int closestNode = newPath.f();
         double distanceSqr = this.getNodeDistanceSquared(newPath.a(this.a));

         for (int i = closestNode + 1; i < newPath.e(); i++) {
            double temp = this.getNodeDistanceSquared(newPath.a(this.a, i));
            if (temp < distanceSqr) {
               distanceSqr = temp;
               closestNode = i;
            }
         }

         newPath.c(closestNode);
      }

      return super.a(newPath, speed);
   }

   private double getNodeDistanceSquared(Vec3D pos) {
      double x = pos.c - this.a.dq();
      double y = pos.d - this.a.ds();
      double z = pos.e - this.a.dw();
      return x * x + y * y + z * z;
   }

   protected void k() {
      Vec3D mobPosition = this.b();
      this.l = this.a.df() > 0.75F ? this.a.df() / 2.0F : 0.75F - this.a.df() / 2.0F;
      Vec3D targetNodePosition = this.c.a(this.a);
      double distX = Math.abs(this.a.dq() - targetNodePosition.c);
      double distY = Math.abs(this.a.ds() - targetNodePosition.d);
      double distZ = Math.abs(this.a.dw() - targetNodePosition.e);
      boolean hasArrivedNode = distX < this.l && distZ < this.l && distY < 1.0;
      boolean canAdvance = hasArrivedNode || this.b(this.c.h().l) && this.shouldTargetNextNodeInDirection(mobPosition);
      if (canAdvance) {
         this.c.a();
      }

      this.b(mobPosition);
   }

   private boolean shouldTargetNextNodeInDirection(Vec3D mobPosition) {
      if (this.c.f() + 1 >= this.c.e()) {
         return false;
      }

      Vec3D targetNodePosition = this.c.a(this.a);
      if (!mobPosition.a((net.minecraft.core.IPosition)targetNodePosition, 2.0)) {
         return false;
      }

      if (this.a(mobPosition, this.c.a(this.a))) {
         return true;
      }

      Vec3D nextNodePosition = this.c.a(this.a, this.c.f() + 1);
      Vec3D nextNodeDir = nextNodePosition.d(targetNodePosition);
      Vec3D mobDir = mobPosition.d(targetNodePosition);
      return nextNodeDir.b(mobDir) > 0.0;
   }
}
