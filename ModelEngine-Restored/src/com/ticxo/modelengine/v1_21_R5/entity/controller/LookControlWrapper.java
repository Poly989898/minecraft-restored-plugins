package com.ticxo.modelengine.v1_21_R5.entity.controller;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MovementOverride;
import io.papermc.paper.entity.activation.ActivationRange;
import lombok.Setter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.phys.Vec3;

public class LookControlWrapper extends LookControl implements LookController {
   private final LookControl original;
   private ModeledEntity modeledEntity;
   @Setter
   private MovementOverride movementOverride;

   public LookControlWrapper(Mob mob, LookControl control) {
      super(mob);
      this.original = control;
   }

   public void setLookAt(Vec3 lookAt) {
      this.original.setLookAt(lookAt);
   }

   public void setLookAt(Entity lookAt) {
      this.original.setLookAt(lookAt);
   }

   public void setLookAt(Entity entity, float value, float value2) {
      this.original.setLookAt(entity, value, value2);
   }

   public void setLookAt(double value, double value2, double value3) {
      this.original.setLookAt(value, value2, value3);
   }

   public void setLookAt(double value, double value2, double value3, float value4, float value5) {
      this.original.setLookAt(value, value2, value3, value4, value5);
   }

   public void tick() {
      if (this.modeledEntity == null) {
         this.modeledEntity = ModelEngineAPI.getModeledEntity(this.mob.getUUID());
      }

      var mountManager = this.getMainManager();
      if (mountManager != null && ((MountManager)mountManager).isControlled()) {
         this.controlledTick(mountManager);
      } else if (this.movementOverride != null) {
         this.movementOverride.updateDirection(this, this.modeledEntity);
      } else {
         this.defaultTick();
      }
   }

   protected <T extends BehaviorManager<? extends Mount> & MountManager> void controlledTick(T manager) {
      org.bukkit.entity.Entity driver = manager.getDriver();
      if (driver != null) {
         MountController controller = ModelEngineAPI.getMountPairManager().getController(driver.getUniqueId());
         if (controller != null) {
            controller.updateDirection(this, manager.getActiveModel());
            return;
         }
      }

      this.defaultTick();
   }

   @Override
   public void defaultTick() {
      if (this.isActive()) {
         this.original.tick();
      }
   }

   public boolean isLookingAtTarget() {
      return this.original.isLookingAtTarget();
   }

   public double getWantedX() {
      return this.original.getWantedX();
   }

   public double getWantedY() {
      return this.original.getWantedY();
   }

   public double getWantedZ() {
      return this.original.getWantedZ();
   }

   @Override
   public void lookAt(double x, double y, double z) {
      double dX = x - this.mob.getX();
      double dY = y - this.mob.getEyeY();
      double dZ = z - this.mob.getZ();
      double hMagnitude = Math.sqrt(dX * dX + dZ * dZ);
      float pitch = (float)Math.toDegrees(Math.atan2(-dY, hMagnitude));
      float yaw = (float)Math.toDegrees(Math.atan2(-dX, dZ));
      this.setPitch(pitch);
      this.setHeadYaw(yaw);
   }

   @Override
   public void setPitch(float pitch) {
      this.mob.setXRot(pitch);
   }

   @Override
   public void setHeadYaw(float yaw) {
      this.mob.setYRot(yaw);
      this.mob.yHeadRot = yaw;
   }

   @Override
   public void setBodyYaw(float yaw) {
      this.mob.yBodyRot = yaw;
   }

   private <T extends BehaviorManager<? extends Mount> & MountManager> T getMainManager() {
      if (this.modeledEntity == null) {
         return null;
      } else {
         return this.modeledEntity.getGlobalBehaviorData((BoneBehaviorType)BoneBehaviorTypes.MOUNT) instanceof MountData mountData
            ? mountData.getMainMountManager()
            : null;
      }
   }

   private boolean isActive() {
      return ActivationRange.checkIfActive(this.mob);
   }
}
