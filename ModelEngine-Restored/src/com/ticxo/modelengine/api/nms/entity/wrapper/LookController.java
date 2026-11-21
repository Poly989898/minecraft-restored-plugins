package com.ticxo.modelengine.api.nms.entity.wrapper;

public interface LookController {
   void lookAt(double x, double y, double z);

   void setPitch(float pitch);

   void setHeadYaw(float yaw);

   void setBodyYaw(float yaw);

   default void setMovementOverride(MovementOverride override) {
   }

   void defaultTick();
}
