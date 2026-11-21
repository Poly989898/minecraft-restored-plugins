package com.ticxo.modelengine.v1_20_R2.entity.controller;

import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.utils.math.TMath;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.control.EntityAIBodyControl;

@Getter
@Setter
public class BodyRotationControlWrapper extends EntityAIBodyControl implements BodyRotationController {
   private final EntityInsentient mob;
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

   public BodyRotationControlWrapper(EntityInsentient mob) {
      super(mob);
      this.mob = mob;
      this.maxHeadAngle = mob.Z();
      this.maxBodyAngle = mob.Z();
      this.minHeadAngle = -this.maxHeadAngle;
      this.minBodyAngle = -this.maxBodyAngle;
   }

   public void a() {
      if (!this.getMob().ev()) {
         if (this.isMoving()) {
            this.mob.aU = this.mob.dB();
            this.rotateHeadIfNecessary();
            this.lastStableYHeadRot = this.mob.aW;
            this.headStableTime = 0;
         } else if (this.notCarryingMobPassengers()) {
            if (Math.abs(this.mob.aW - this.lastStableYHeadRot) > this.stableAngle) {
               this.headStableTime = 0;
               this.lastStableYHeadRot = this.mob.aW;
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
      return this.mob.aW;
   }

   @Override
   public void setYHeadRot(float rot) {
      this.mob.aW = rot;
   }

   @Override
   public float getXHeadRot() {
      return this.mob.dD();
   }

   @Override
   public void setXHeadRot(float rot) {
      this.mob.s(rot);
   }

   @Override
   public float getYBodyRot() {
      return this.mob.aU;
   }

   @Override
   public void setYBodyRot(float rot) {
      this.mob.aU = rot;
   }

   private void rotateBodyIfNecessary() {
      this.mob.aU = TMath.rotateIfNecessary(this.mob.aU, this.mob.aW, this.isBodyClampUneven ? this.minBodyAngle : -this.maxBodyAngle, this.maxBodyAngle);
   }

   private void rotateHeadIfNecessary() {
      this.mob.aW = TMath.rotateIfNecessary(this.mob.aW, this.mob.aU, this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle, this.maxHeadAngle);
   }

   private void rotateHeadTowardsFront() {
      float ratio = (float)(this.headStableTime - this.rotationDelay) / this.rotationDuration;
      float clampedRatio = TMath.clamp(ratio, 0.0F, 1.0F);
      float maxClamp = this.maxHeadAngle * (1.0F - clampedRatio);
      float minClamp = (this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle) * (1.0F - clampedRatio);
      this.mob.aU = TMath.rotateIfNecessary(this.mob.aU, this.mob.aW, minClamp, maxClamp);
   }

   private boolean notCarryingMobPassengers() {
      return !(this.mob.cQ() instanceof EntityInsentient);
   }

   private boolean isMoving() {
      double dX = this.mob.dq() - this.mob.K;
      double dZ = this.mob.dw() - this.mob.M;
      return dX * dX + dZ * dZ > 2.5000003E-7F;
   }
}
