package com.ticxo.modelengine.api.nms.impl;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.utils.math.TMath;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

@Getter
@Setter
public class DefaultBodyRotationController implements BodyRotationController {
   private final BaseEntity<?> entity;
   private float xHeadRot;
   private float yHeadRot;
   private float yBodyRot;
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

   public DefaultBodyRotationController(BaseEntity<?> entity) {
      this.entity = entity;
      this.syncFromBase();
      this.maxHeadAngle = 75.0F;
      this.maxBodyAngle = 75.0F;
      this.minHeadAngle = -this.maxHeadAngle;
      this.minBodyAngle = -this.maxBodyAngle;
   }

   public void syncFromBase() {
      this.xHeadRot = this.entity.getXHeadRot();
      this.yHeadRot = this.entity.getYHeadRot();
      this.yBodyRot = this.entity.getYBodyRot();
   }

   @Override
   public void tick() {
      this.xHeadRot = this.entity.getXHeadRot();
      this.yHeadRot = this.entity.getYHeadRot();
      if (this.entity.isWalking()) {
         this.yBodyRot = this.entity.getYRot();
         this.rotateHeadIfNecessary();
         this.lastStableYHeadRot = this.yHeadRot;
         this.headStableTime = 0;
      } else if (this.notCarryingMobPassengers()) {
         if (Math.abs(this.yHeadRot - this.lastStableYHeadRot) > this.stableAngle) {
            this.headStableTime = 0;
            this.lastStableYHeadRot = this.yHeadRot;
            this.rotateBodyIfNecessary();
         } else if (!this.playerMode) {
            this.headStableTime++;
            if (this.headStableTime > this.rotationDelay) {
               this.rotateHeadTowardsFront();
            }
         }
      }
   }

   private void rotateBodyIfNecessary() {
      this.yBodyRot = TMath.rotateIfNecessary(this.yBodyRot, this.yHeadRot, this.isBodyClampUneven ? this.minBodyAngle : -this.maxBodyAngle, this.maxBodyAngle);
   }

   private void rotateHeadIfNecessary() {
      this.yHeadRot = TMath.rotateIfNecessary(this.yHeadRot, this.yBodyRot, this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle, this.maxHeadAngle);
   }

   private void rotateHeadTowardsFront() {
      float ratio = (float)(this.headStableTime - this.rotationDelay) / this.rotationDuration;
      float clampedRatio = TMath.clamp(ratio, 0.0F, 1.0F);
      float maxClamp = this.maxHeadAngle * (1.0F - clampedRatio);
      float minClamp = (this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle) * (1.0F - clampedRatio);
      this.yBodyRot = TMath.rotateIfNecessary(this.yBodyRot, this.yHeadRot, minClamp, maxClamp);
   }

   private boolean notCarryingMobPassengers() {
      for (Entity entity : this.entity.getPassengers()) {
         if (entity instanceof Mob) {
            return false;
         }
      }

      return true;
   }
}
