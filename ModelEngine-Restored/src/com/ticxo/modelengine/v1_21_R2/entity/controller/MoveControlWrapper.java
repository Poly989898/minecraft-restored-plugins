package com.ticxo.modelengine.v1_21_R2.entity.controller;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.rootmotion.RootMotionDelta;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.GlobalBehaviorData;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_21_R2.NMSFields;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Input;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MoveControlWrapper extends MoveControl implements MoveController {
   protected final MoveControl original;
   protected final Queue<Runnable> runnables = new ConcurrentLinkedQueue<>();
   protected boolean isOnGround;

   public MoveControlWrapper(Mob mob, MoveControl control) {
      super(mob);
      this.original = control;
   }

   public boolean hasWanted() {
      return this.original.hasWanted();
   }

   public double getSpeedModifier() {
      return this.original.getSpeedModifier();
   }

   public void setWantedPosition(double x, double y, double z, double speed) {
      this.original.setWantedPosition(x, y, z, speed);
   }

   public void strafe(float forward, float sideways) {
      this.original.strafe(forward, sideways);
   }

   public void tick() {
      this.isOnGround = this.mob.onGround();
      ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(this.mob.getUUID());
      if (modeledEntity == null) {
         this.defaultTick();
      } else {
         GlobalBehaviorData mountData = modeledEntity.getMountData();
         var mainMountManager = mountData == null ? null : ((MountData)mountData).getMainMountManager();
         if (mainMountManager != null && ((MountManager)mainMountManager).isControlled()) {
            this.mob.setOnGround(true);
            this.disableWaterJumping();
            this.driverTick(mainMountManager);
         } else {
            this.defaultTick();
         }

         this.passengerTick(modeledEntity, mainMountManager);

         while (!this.runnables.isEmpty()) {
            this.runnables.poll().run();
         }

         Vector mobMotion = this.toVector();
         RootMotionDelta rootMotion = modeledEntity.getRootMotionHandler().calculateRootMotion(mobMotion);
         this.fromVector(mobMotion);
         if (rootMotion != null) {
            Vector velocity = rootMotion.delta();
            if (!velocity.isZero() || !rootMotion.onGround()) {
               this.nullifyFallDistance();
               if (!TMath.isSimilar((float)velocity.getY(), 0.0F) || !rootMotion.onGround()) {
                  this.mob.setDeltaMovement(this.mob.getDeltaMovement().multiply(1.0, 0.0, 1.0));
               }

               this.mob.move(MoverType.SELF, new Vec3(velocity.getX(), velocity.getY(), velocity.getZ()));
            }
         }
      }
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

   protected <T extends BehaviorManager<? extends Mount> & MountManager> void driverTick(T manager) {
      this.mob.setZza(0.0F);
      this.mob.setXxa(0.0F);
      this.updateRider(manager.getDriver(), manager.getActiveModel(), manager.getDriverBone(), MountController::updateDriverMovement);
   }

   protected <T extends BehaviorManager<? extends Mount> & MountManager> void passengerTick(ModeledEntity modeledEntity, T manager) {
      for (ActiveModel activeModel : modeledEntity.getModels().values()) {
         activeModel.getMountManager()
            .ifPresent(
               mountManager -> {
                  for (BoneBehavior seat : ((MountManager)mountManager).getSeats().values()) {
                     for (Entity passenger : ((Mount)seat).getPassengers()) {
                        this.updateRider(passenger, activeModel, (Mount)seat, MountController::updatePassengerMovement);
                     }
                  }

                  if (mountManager != manager && ((MountManager)mountManager).isControlled()) {
                     this.updateRider(
                        ((MountManager)mountManager).getDriver(),
                        activeModel,
                        ((MountManager)mountManager).getDriverBone(),
                        MountController::updatePassengerMovement
                     );
                  }
               }
            );
      }
   }

   private void updateRider(Entity entity, ActiveModel activeModel, Mount mountBone, TriConsumer<MountController, MoveController, ActiveModel> updateMethod) {
      MountController controller = this.getController(entity.getUniqueId());
      if (controller != null) {
         if (controller.getInput() == null) {
            if (entity instanceof Player player) {
               Input playerInput = player.getCurrentInput();
               controller.setInput(
                  new MountController.MountInput(
                     playerInput.isForward(),
                     playerInput.isBackward(),
                     playerInput.isLeft(),
                     playerInput.isRight(),
                     playerInput.isJump(),
                     playerInput.isSneak(),
                     playerInput.isSprint()
                  )
               );
            } else {
               controller.setInput(new MountController.MountInput());
            }
         }

         updateMethod.accept(controller, this, activeModel);
      }
   }

   @Override
   public void defaultTick() {
      this.original.tick();
   }

   private void disableWaterJumping() {
      if (this.mob.isInWater()) {
         ReflectionUtils.set(this.mob, NMSFields.LIVING_ENTITY_noJumpDelay, 1);
      }
   }

   @Override
   public void move(float side, float up, float front, float speedModifier) {
      float speed = this.getSpeed();
      this.mob.setSpeed(speed * speedModifier);
      this.mob.setZza(front);
      this.mob.setYya(up);
      this.mob.setXxa(side);
   }

   @Override
   public void globalMove(float x, float y, float z, float speedModifier) {
      float speed = this.getSpeed();
      this.mob.setSpeed(speed * speedModifier);
      Vec3 vec = new Vec3(x, y, z).yRot(-this.mob.getYRot() * (float) (Math.PI / 180.0));
      this.mob.setXxa((float)vec.x);
      this.mob.setYya((float)vec.y);
      this.mob.setZza((float)vec.z);
   }

   private void fromVector(Vector vector) {
      Vec3 vec = new Vec3(vector.getX(), vector.getY(), vector.getZ()).yRot(-this.mob.getYRot() * (float) (Math.PI / 180.0));
      this.mob.setXxa((float)vec.x);
      this.mob.setYya((float)vec.y);
      this.mob.setZza((float)vec.z);
   }

   private Vector toVector() {
      return new Vector(
            Float.isNaN(this.mob.xxa) ? 0.0F : this.mob.xxa, Float.isNaN(this.mob.yya) ? 0.0F : this.mob.yya, Float.isNaN(this.mob.zza) ? 0.0F : this.mob.zza
         )
         .rotateAroundY(this.mob.getYRot() * (float) (Math.PI / 180.0));
   }

   @Override
   public void jump() {
      this.mob.getJumpControl().jump();
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.mob.setDeltaMovement(x, y, z);
   }

   @Override
   public void addVelocity(double x, double y, double z) {
      this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(x, y, z));
   }

   @Override
   public void nullifyFallDistance() {
      this.mob.resetFallDistance();
   }

   @Override
   public boolean isOnGround() {
      return this.isOnGround;
   }

   @Override
   public boolean isInWater() {
      return this.mob.isInWater();
   }

   @Override
   public float getSpeed() {
      return (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
   }

   @Override
   public Vector getVelocity() {
      return CraftVector.toBukkit(this.mob.getDeltaMovement());
   }

   @Override
   public void queuePostTick(Runnable runnable) {
      this.runnables.add(runnable);
   }

   private MountController getController(UUID uuid) {
      return ModelEngineAPI.getMountPairManager().getController(uuid);
   }
}
