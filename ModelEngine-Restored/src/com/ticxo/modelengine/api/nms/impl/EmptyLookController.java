package com.ticxo.modelengine.api.nms.impl;

import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;

public class EmptyLookController implements LookController {
   @Override
   public void lookAt(double x, double y, double z) {
   }

   @Override
   public void setPitch(float pitch) {
   }

   @Override
   public void setHeadYaw(float yaw) {
   }

   @Override
   public void setBodyYaw(float yaw) {
   }

   @Override
   public void defaultTick() {
   }
}
