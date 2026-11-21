package com.ticxo.modelengine.api.nms.entity.wrapper;

import com.ticxo.modelengine.api.ModelEngineAPI;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface MoveController {
   void move(float side, float up, float front, float speedModifier);

   void globalMove(float x, float y, float z, float speedModifier);

   void jump();

   void setVelocity(double x, double y, double z);

   void addVelocity(double x, double y, double z);

   void nullifyFallDistance();

   default void movePassenger(Entity entity, double x, double y, double z) {
      ModelEngineAPI.getEntityHandler().movePassenger(entity, x, y, z);
   }

   boolean isOnGround();

   boolean isInWater();

   float getSpeed();

   Vector getVelocity();

   void queuePostTick(Runnable runnable);

   default void setMovementOverride(MovementOverride override) {
   }

   default boolean ticksMountController() {
      return true;
   }

   void defaultTick();
}
