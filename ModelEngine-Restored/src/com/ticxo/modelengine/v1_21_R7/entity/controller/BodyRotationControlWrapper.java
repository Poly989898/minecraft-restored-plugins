package com.ticxo.modelengine.v1_21_R7.entity.controller;

import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.utils.math.TMath;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

@Getter
@Setter
public class BodyRotationControlWrapper extends BodyRotationControl implements BodyRotationController {
   private final Mob mob;
   private boolean isHeadClampUneven;
   private boolean isBodyClampUneven;
   private float maxHeadAngle;
   private float maxBodyAngle;
   private float minHeadAngle;
   private float minBodyAngle;
   private boolean playerMode;
   private float stableAngle = 15.0F;
   private int rotationDelay = 10;
   private int rotationDuration = 10;
   private int headStableTime;
   private float lastStableYHeadRot;

   public BodyRotationControlWrapper(Mob mob) {
      super(mob);
      this.mob = mob;
      this.maxHeadAngle = mob.getMaxHeadYRot();
      this.maxBodyAngle = mob.getMaxHeadYRot();
      this.minHeadAngle = -this.maxHeadAngle;
      this.minBodyAngle = -this.maxBodyAngle;
   }

   public void clientTick() {
      if (!this.getMob().isDeadOrDying()) {
         if (this.isMoving()) {
            this.mob.yBodyRot = this.mob.getYRot();
            this.rotateHeadIfNecessary();
            this.lastStableYHeadRot = this.mob.yHeadRot;
            this.headStableTime = 0;
         } else if (this.notCarryingMobPassengers()) {
            if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > this.stableAngle) {
               this.headStableTime = 0;
               this.lastStableYHeadRot = this.mob.yHeadRot;
               this.rotateBodyIfNecessary();
            } else if (!this.playerMode) {
               this.headStableTime++;
               if (this.headStableTime > this.rotationDelay) {
                  this.rotateHeadTowardsFront();
               }
            }
         }
      }
   }

   @Override
   public float getYHeadRot() {
      return this.mob.yHeadRot;
   }

   @Override
   public void setYHeadRot(float rot) {
      this.mob.yHeadRot = rot;
   }

   @Override
   public float getXHeadRot() {
      return this.mob.getXRot();
   }

   @Override
   public void setXHeadRot(float rot) {
      this.mob.setXRot(rot);
   }

   @Override
   public float getYBodyRot() {
      return this.mob.yBodyRot;
   }

   @Override
   public void setYBodyRot(float rot) {
      this.mob.yBodyRot = rot;
   }

   private void rotateBodyIfNecessary() {
      this.mob.yBodyRot = TMath.rotateIfNecessary(
         this.mob.yBodyRot, this.mob.yHeadRot, this.isBodyClampUneven ? this.minBodyAngle : -this.maxBodyAngle, this.maxBodyAngle
      );
   }

   private void rotateHeadIfNecessary() {
      this.mob.yHeadRot = TMath.rotateIfNecessary(
         this.mob.yHeadRot, this.mob.yBodyRot, this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle, this.maxHeadAngle
      );
   }

   private void rotateHeadTowardsFront() {
      float ratio = (float)(this.headStableTime - this.rotationDelay) / this.rotationDuration;
      float clampedRatio = TMath.clamp(ratio, 0.0F, 1.0F);
      float maxClamp = this.maxHeadAngle * (1.0F - clampedRatio);
      float minClamp = (this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle) * (1.0F - clampedRatio);
      this.mob.yBodyRot = TMath.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, minClamp, maxClamp);
   }

   private boolean notCarryingMobPassengers() {
      return !(this.mob.getFirstPassenger() instanceof Mob);
   }

   private boolean isMoving() {
      double dX = this.mob.getX() - this.mob.xo;
      double dZ = this.mob.getZ() - this.mob.zo;
      return dX * dX + dZ * dZ > 2.5000003E-7F;
   }
}
