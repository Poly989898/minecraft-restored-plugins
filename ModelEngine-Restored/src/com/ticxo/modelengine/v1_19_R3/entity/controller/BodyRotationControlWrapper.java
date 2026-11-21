package com.ticxo.modelengine.v1_19_R3.entity.controller;

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
      this.maxHeadAngle = mob.W();
      this.maxBodyAngle = mob.W();
      this.minHeadAngle = -this.maxHeadAngle;
      this.minBodyAngle = -this.maxBodyAngle;
   }

   public void a() {
      if (!this.getMob().ep()) {
         if (this.isMoving()) {
            this.mob.aT = this.mob.dw();
            this.rotateHeadIfNecessary();
            this.lastStableYHeadRot = this.mob.aV;
            this.headStableTime = 0;
         } else if (this.notCarryingMobPassengers()) {
            if (Math.abs(this.mob.aV - this.lastStableYHeadRot) > this.stableAngle) {
               this.headStableTime = 0;
               this.lastStableYHeadRot = this.mob.aV;
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
      return this.mob.aV;
   }

   @Override
   public void setYHeadRot(float rot) {
      this.mob.aV = rot;
   }

   @Override
   public float getXHeadRot() {
      return this.mob.dy();
   }

   @Override
   public void setXHeadRot(float rot) {
      this.mob.e(rot);
   }

   @Override
   public float getYBodyRot() {
      return this.mob.aT;
   }

   @Override
   public void setYBodyRot(float rot) {
      this.mob.aT = rot;
   }

   private void rotateBodyIfNecessary() {
      this.mob.aT = TMath.rotateIfNecessary(this.mob.aT, this.mob.aV, this.isBodyClampUneven ? this.minBodyAngle : -this.maxBodyAngle, this.maxBodyAngle);
   }

   private void rotateHeadIfNecessary() {
      this.mob.aV = TMath.rotateIfNecessary(this.mob.aV, this.mob.aT, this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle, this.maxHeadAngle);
   }

   private void rotateHeadTowardsFront() {
      float ratio = (float)(this.headStableTime - this.rotationDelay) / this.rotationDuration;
      float clampedRatio = TMath.clamp(ratio, 0.0F, 1.0F);
      float maxClamp = this.maxHeadAngle * (1.0F - clampedRatio);
      float minClamp = (this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle) * (1.0F - clampedRatio);
      this.mob.aT = TMath.rotateIfNecessary(this.mob.aT, this.mob.aV, minClamp, maxClamp);
   }

   private boolean notCarryingMobPassengers() {
      return !(this.mob.cN() instanceof EntityInsentient);
   }

   private boolean isMoving() {
      double dX = this.mob.dl() - this.mob.I;
      double dZ = this.mob.dr() - this.mob.K;
      return dX * dX + dZ * dZ > 2.5000003E-7F;
   }
}
