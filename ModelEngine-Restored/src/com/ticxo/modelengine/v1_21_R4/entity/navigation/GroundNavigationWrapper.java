package com.ticxo.modelengine.v1_21_R4.entity.navigation;

import com.ticxo.modelengine.api.ModelEngineAPI;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GroundNavigationWrapper extends GroundPathNavigation {
   public GroundNavigationWrapper(Mob mob, GroundPathNavigation oldNav) {
      super(mob, mob.level());
      this.setCanFloat(oldNav.canFloat());
      oldNav.getNodeEvaluator().setCanOpenDoors(oldNav.getNodeEvaluator().canOpenDoors());
      oldNav.getNodeEvaluator().setCanPassDoors(oldNav.getNodeEvaluator().canPassDoors());
   }

   public boolean moveTo(@Nullable Path newPath, double speed) {
      if (!ModelEngineAPI.getConfigCache().isOverrideMoveTo()) {
         return super.moveTo(newPath, speed);
      }

      if (newPath != null && !newPath.sameAs(this.path)) {
         int closestNode = newPath.getNextNodeIndex();
         double distanceSqr = this.getNodeDistanceSquared(newPath.getNextEntityPos(this.mob));

         for (int i = closestNode + 1; i < newPath.getNodeCount(); i++) {
            double temp = this.getNodeDistanceSquared(newPath.getEntityPosAtNode(this.mob, i));
            if (temp < distanceSqr) {
               distanceSqr = temp;
               closestNode = i;
            }
         }

         newPath.setNextNodeIndex(closestNode);
      }

      return super.moveTo(newPath, speed);
   }

   private double getNodeDistanceSquared(Vec3 pos) {
      double x = pos.x - this.mob.getX();
      double y = pos.y - this.mob.getY();
      double z = pos.z - this.mob.getZ();
      return x * x + y * y + z * z;
   }

   protected void followThePath() {
      Vec3 mobPosition = this.getTempMobPos();
      this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
      Vec3 targetNodePosition = this.path.getNextEntityPos(this.mob);
      double distX = Math.abs(this.mob.getX() - targetNodePosition.x);
      double distY = Math.abs(this.mob.getY() - targetNodePosition.y);
      double distZ = Math.abs(this.mob.getZ() - targetNodePosition.z);
      boolean hasArrivedNode = distX < this.maxDistanceToWaypoint && distZ < this.maxDistanceToWaypoint && distY < 1.0;
      boolean canAdvance = hasArrivedNode || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(mobPosition);
      if (canAdvance) {
         this.path.advance();
      }

      this.doStuckDetection(mobPosition);
   }

   private boolean shouldTargetNextNodeInDirection(Vec3 mobPosition) {
      if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
         return false;
      }

      Vec3 targetNodePosition = this.path.getNextEntityPos(this.mob);
      if (!mobPosition.closerThan(targetNodePosition, 2.0)) {
         return false;
      }

      if (this.canMoveDirectly(mobPosition, this.path.getNextEntityPos(this.mob))) {
         return true;
      }

      Vec3 nextNodePosition = this.path.getEntityPosAtNode(this.mob, this.path.getNextNodeIndex() + 1);
      Vec3 nextNodeDir = nextNodePosition.subtract(targetNodePosition);
      Vec3 mobDir = mobPosition.subtract(targetNodePosition);
      return nextNodeDir.dot(mobDir) > 0.0;
   }
}
