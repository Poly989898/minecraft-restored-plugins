package com.ticxo.modelengine.api.mount.controller;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public interface MountController {
   Entity getEntity();

   MountController.MountInput getInput();

   void setInput(MountController.MountInput input);

   Mount getMount();

   void setCanDamageMount(boolean canDamageMount);

   boolean canDamageMount();

   void setCanInteractMount(boolean canInteractMount);

   boolean canInteractMount();

   void updateDriverMovement(MoveController controller, ActiveModel model);

   void updatePassengerMovement(MoveController controller, ActiveModel model);

   default boolean shouldEngineTickMovement() {
      return false;
   }

   default void updateRiderPosition(MoveController controller) {
      Vector3f pos = this.getMount().getGlobalLocation();
      controller.movePassenger(this.getEntity(), pos.x, pos.y, pos.z);
   }

   void updateDirection(LookController controller, ActiveModel model);

   class MountInput {
      private Float side;
      private Float front;
      private boolean forward;
      private boolean backward;
      private boolean left;
      private boolean right;
      private boolean jump;
      private boolean sneak;
      private boolean sprint;
      private boolean updated;

      public MountInput() {
         this(false, false, false, false, false, false, false);
      }

      public MountInput(float side, float front, boolean jump, boolean sneak) {
         this.side = side;
         this.front = front;
         this.jump = jump;
         this.sneak = sneak;
      }

      public MountInput(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
         this.forward = forward;
         this.backward = backward;
         this.left = left;
         this.right = right;
         this.jump = jump;
         this.sneak = sneak;
         this.sprint = sprint;
      }

      public float getFront() {
         if (this.front != null) {
            return this.front;
         } else {
            return this.forward ? 1.0F : (this.backward ? -1.0F : 0.0F);
         }
      }

      public float getSide() {
         if (this.side != null) {
            return this.side;
         } else {
            return this.left ? 1.0F : (this.right ? -1.0F : 0.0F);
         }
      }

      @Generated
      public boolean isForward() {
         return this.forward;
      }

      @Generated
      public boolean isBackward() {
         return this.backward;
      }

      @Generated
      public boolean isLeft() {
         return this.left;
      }

      @Generated
      public boolean isRight() {
         return this.right;
      }

      @Generated
      public boolean isJump() {
         return this.jump;
      }

      @Generated
      public boolean isSneak() {
         return this.sneak;
      }

      @Generated
      public boolean isSprint() {
         return this.sprint;
      }

      @Generated
      public boolean isUpdated() {
         return this.updated;
      }

      @Generated
      public void setSide(Float side) {
         this.side = side;
      }

      @Generated
      public void setFront(Float front) {
         this.front = front;
      }

      @Generated
      public void setForward(boolean forward) {
         this.forward = forward;
      }

      @Generated
      public void setBackward(boolean backward) {
         this.backward = backward;
      }

      @Generated
      public void setLeft(boolean left) {
         this.left = left;
      }

      @Generated
      public void setRight(boolean right) {
         this.right = right;
      }

      @Generated
      public void setJump(boolean jump) {
         this.jump = jump;
      }

      @Generated
      public void setSneak(boolean sneak) {
         this.sneak = sneak;
      }

      @Generated
      public void setSprint(boolean sprint) {
         this.sprint = sprint;
      }

      @Generated
      public void setUpdated(boolean updated) {
         this.updated = updated;
      }
   }
}
