package com.ticxo.modelengine.v1_20_R1.entity.controller;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.phys.Vec3D;
import org.spigotmc.ActivationRange;

public class LookControlWrapper extends ControllerLook implements LookController {
   private final ControllerLook original;
   private ModeledEntity modeledEntity;

   public LookControlWrapper(EntityInsentient mob, ControllerLook control) {
      super(mob);
      this.original = control;
   }

   public void a(Vec3D vec3D) {
      this.original.a(vec3D);
   }

   public void a(Entity entity) {
      this.original.a(entity);
   }

   public void a(Entity entity, float value, float value2) {
      this.original.a(entity, value, value2);
   }

   public void a(double value, double value2, double value3) {
      this.original.a(value, value2, value3);
   }

   public void a(double value, double value2, double value3, float value4, float value5) {
      this.original.a(value, value2, value3, value4, value5);
   }

   public void a() {
      if (this.modeledEntity == null) {
         this.modeledEntity = ModelEngineAPI.getModeledEntity(this.a.ct());
      }

      var mountManager = this.getMainManager();
      if (mountManager != null && ((MountManager)mountManager).isControlled()) {
         this.controlledTick(mountManager);
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
         this.original.a();
      }
   }

   public boolean d() {
      return this.original.d();
   }

   public double e() {
      return this.original.e();
   }

   public double f() {
      return this.original.f();
   }

   public double g() {
      return this.original.g();
   }

   @Override
   public void lookAt(double x, double y, double z) {
      double dX = x - this.a.dn();
      double dY = y - this.a.dr();
      double dZ = z - this.a.dt();
      double hMagnitude = Math.sqrt(dX * dX + dZ * dZ);
      float pitch = (float)Math.toDegrees(Math.atan2(-dY, hMagnitude));
      float yaw = (float)Math.toDegrees(Math.atan2(-dX, dZ));
      this.setPitch(pitch);
      this.setHeadYaw(yaw);
   }

   @Override
   public void setPitch(float pitch) {
      this.a.b_(pitch);
   }

   @Override
   public void setHeadYaw(float yaw) {
      this.a.a_(yaw);
      this.a.aX = yaw;
   }

   @Override
   public void setBodyYaw(float yaw) {
      this.a.aV = yaw;
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
      return ActivationRange.checkIfActive(this.a);
   }
}
