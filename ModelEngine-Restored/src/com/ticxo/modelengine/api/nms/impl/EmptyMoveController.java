package com.ticxo.modelengine.api.nms.impl;

import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import org.bukkit.util.Vector;

public class EmptyMoveController implements MoveController {
   @Override
   public void move(float side, float up, float front, float speedModifier) {
   }

   @Override
   public void globalMove(float x, float y, float z, float speedModifier) {
   }

   @Override
   public void jump() {
   }

   @Override
   public void setVelocity(double x, double y, double z) {
   }

   @Override
   public void addVelocity(double x, double y, double z) {
   }

   @Override
   public void nullifyFallDistance() {
   }

   @Override
   public boolean isOnGround() {
      return false;
   }

   @Override
   public boolean isInWater() {
      return false;
   }

   @Override
   public float getSpeed() {
      return 0.0F;
   }

   @Override
   public Vector getVelocity() {
      return new Vector();
   }

   @Override
   public void queuePostTick(Runnable runnable) {
      runnable.run();
   }

   @Override
   public boolean ticksMountController() {
      return false;
   }

   @Override
   public void defaultTick() {
   }
}
