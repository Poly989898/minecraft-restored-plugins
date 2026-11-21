package com.ticxo.modelengine.core.data;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.nms.impl.DefaultBodyRotationController;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.core.model.ModeledEntityImpl;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Generated;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class V3Data {
   private int renderDistance;
   private boolean isHeadClampUneven;
   private boolean isBodyClampUneven;
   private float maxHeadAngle;
   private float maxBodyAngle;
   private float minHeadAngle;
   private float minBodyAngle;
   private boolean playerMode;
   private float stableAngle;
   private int rotationDelay;
   private int rotationDuration;
   private boolean canSteer;
   private boolean canRide;
   private String mountModelId;
   private String mountDriverBone;
   private boolean isBaseEntityVisible;
   private boolean modelRotationLock;
   private Double stepHeight;
   private Hitbox hitbox;
   private final Map<String, V3Data.ModelData> models = Maps.newConcurrentMap();

   public SavedData convert(@Nullable Location location) {
      Dummy<Object> dummy = new Dummy<>();
      dummy.setRenderRadius(this.renderDistance);
      dummy.setLocation(location);
      if (this.stepHeight != null) {
         dummy.setMaxStepHeight(this.stepHeight);
      }

      DefaultBodyRotationController bodyController = dummy.getBodyRotationController();
      bodyController.setHeadClampUneven(this.isHeadClampUneven);
      bodyController.setBodyClampUneven(this.isBodyClampUneven);
      bodyController.setMaxHeadAngle(this.maxHeadAngle);
      bodyController.setMaxBodyAngle(this.maxBodyAngle);
      bodyController.setMinHeadAngle(this.minHeadAngle);
      bodyController.setMinBodyAngle(this.minBodyAngle);
      bodyController.setPlayerMode(this.playerMode);
      bodyController.setStableAngle(this.stableAngle);
      bodyController.setRotationDelay(this.rotationDelay);
      bodyController.setRotationDuration(this.rotationDuration);
      var modeledEntity = new ModeledEntityImpl(dummy, null) {
         @Override
         public void registerSelf() {
         }
      };
      modeledEntity.setBaseEntityVisible(this.isBaseEntityVisible);
      modeledEntity.setModelRotationLocked(this.modelRotationLock);
      this.models
         .forEach(
            (modelId, modelData) -> {
               ActiveModel activeModel = ModelEngineAPI.createActiveModel(modelId);
               if (activeModel != null) {
                  activeModel.setCanHurt(modelData.canHurt);
                  activeModel.setLockPitch(modelData.lockPitch);
                  activeModel.setLockYaw(modelData.lockYaw);
                  modelData.defaultStates
                     .forEach(
                        (modelState, property) -> activeModel.getAnimationHandler()
                           .setDefaultProperty(
                              new AnimationHandler.DefaultProperty(modelState, property.stateId, property.lerpIn, property.lerpOut, property.speed)
                           )
                     );
                  ModelBlueprint blueprint = activeModel.getBlueprint();
                  modelData.states.forEach((state, stateData) -> {
                     BlueprintAnimation animation = blueprint.getAnimations().get(state);
                     if (animation != null) {
                        SimpleProperty property = new SimpleProperty(activeModel, animation, stateData.lerpIn, stateData.lerpOut, stateData.speed);
                        property.setPhase(stateData.phase);
                        property.setForceLoopMode(stateData.forceLoopMode);
                        property.setForceOverride(stateData.forceOverride ? BlueprintAnimation.OverrideMode.OVERRIDE : null);
                        activeModel.getAnimationHandler().playAnimation(property, true);
                     }
                  });
                  modeledEntity.addModel(activeModel, false).ifPresent(ActiveModel::destroy);
               }
            }
         );
      if (modeledEntity.getGlobalBehaviorData(BoneBehaviorTypes.MOUNT) instanceof MountData mountData) {
         BehaviorManager manager = mountData.getMainMountManager();
         if (manager != null) {
            ((MountManager)manager).setCanDrive(this.canSteer);
            ((MountManager)manager).setCanRide(this.canRide);
         }
      }

      SavedData data = modeledEntity.save().orElse(null);
      modeledEntity.destroy();
      return data;
   }

   @Generated
   public int getRenderDistance() {
      return this.renderDistance;
   }

   @Generated
   public boolean isHeadClampUneven() {
      return this.isHeadClampUneven;
   }

   @Generated
   public boolean isBodyClampUneven() {
      return this.isBodyClampUneven;
   }

   @Generated
   public float getMaxHeadAngle() {
      return this.maxHeadAngle;
   }

   @Generated
   public float getMaxBodyAngle() {
      return this.maxBodyAngle;
   }

   @Generated
   public float getMinHeadAngle() {
      return this.minHeadAngle;
   }

   @Generated
   public float getMinBodyAngle() {
      return this.minBodyAngle;
   }

   @Generated
   public boolean isPlayerMode() {
      return this.playerMode;
   }

   @Generated
   public float getStableAngle() {
      return this.stableAngle;
   }

   @Generated
   public int getRotationDelay() {
      return this.rotationDelay;
   }

   @Generated
   public int getRotationDuration() {
      return this.rotationDuration;
   }

   @Generated
   public boolean isCanSteer() {
      return this.canSteer;
   }

   @Generated
   public boolean isCanRide() {
      return this.canRide;
   }

   @Generated
   public String getMountModelId() {
      return this.mountModelId;
   }

   @Generated
   public String getMountDriverBone() {
      return this.mountDriverBone;
   }

   @Generated
   public boolean isBaseEntityVisible() {
      return this.isBaseEntityVisible;
   }

   @Generated
   public boolean isModelRotationLock() {
      return this.modelRotationLock;
   }

   @Generated
   public Double getStepHeight() {
      return this.stepHeight;
   }

   @Generated
   public Hitbox getHitbox() {
      return this.hitbox;
   }

   @Generated
   public Map<String, V3Data.ModelData> getModels() {
      return this.models;
   }

   @Generated
   public void setRenderDistance(int renderDistance) {
      this.renderDistance = renderDistance;
   }

   @Generated
   public void setHeadClampUneven(boolean isHeadClampUneven) {
      this.isHeadClampUneven = isHeadClampUneven;
   }

   @Generated
   public void setBodyClampUneven(boolean isBodyClampUneven) {
      this.isBodyClampUneven = isBodyClampUneven;
   }

   @Generated
   public void setMaxHeadAngle(float maxHeadAngle) {
      this.maxHeadAngle = maxHeadAngle;
   }

   @Generated
   public void setMaxBodyAngle(float maxBodyAngle) {
      this.maxBodyAngle = maxBodyAngle;
   }

   @Generated
   public void setMinHeadAngle(float minHeadAngle) {
      this.minHeadAngle = minHeadAngle;
   }

   @Generated
   public void setMinBodyAngle(float minBodyAngle) {
      this.minBodyAngle = minBodyAngle;
   }

   @Generated
   public void setPlayerMode(boolean playerMode) {
      this.playerMode = playerMode;
   }

   @Generated
   public void setStableAngle(float stableAngle) {
      this.stableAngle = stableAngle;
   }

   @Generated
   public void setRotationDelay(int rotationDelay) {
      this.rotationDelay = rotationDelay;
   }

   @Generated
   public void setRotationDuration(int rotationDuration) {
      this.rotationDuration = rotationDuration;
   }

   @Generated
   public void setCanSteer(boolean canSteer) {
      this.canSteer = canSteer;
   }

   @Generated
   public void setCanRide(boolean canRide) {
      this.canRide = canRide;
   }

   @Generated
   public void setMountModelId(String mountModelId) {
      this.mountModelId = mountModelId;
   }

   @Generated
   public void setMountDriverBone(String mountDriverBone) {
      this.mountDriverBone = mountDriverBone;
   }

   @Generated
   public void setBaseEntityVisible(boolean isBaseEntityVisible) {
      this.isBaseEntityVisible = isBaseEntityVisible;
   }

   @Generated
   public void setModelRotationLock(boolean modelRotationLock) {
      this.modelRotationLock = modelRotationLock;
   }

   @Generated
   public void setStepHeight(Double stepHeight) {
      this.stepHeight = stepHeight;
   }

   @Generated
   public void setHitbox(Hitbox hitbox) {
      this.hitbox = hitbox;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof V3Data other)) {
         return false;
      } else {
         if (!other.canEqual(this)) {
            return false;
         }

         if (this.getRenderDistance() != other.getRenderDistance()) {
            return false;
         }

         if (this.isHeadClampUneven() != other.isHeadClampUneven()) {
            return false;
         }

         if (this.isBodyClampUneven() != other.isBodyClampUneven()) {
            return false;
         }

         if (Float.compare(this.getMaxHeadAngle(), other.getMaxHeadAngle()) != 0) {
            return false;
         }

         if (Float.compare(this.getMaxBodyAngle(), other.getMaxBodyAngle()) != 0) {
            return false;
         }

         if (Float.compare(this.getMinHeadAngle(), other.getMinHeadAngle()) != 0) {
            return false;
         }

         if (Float.compare(this.getMinBodyAngle(), other.getMinBodyAngle()) != 0) {
            return false;
         }

         if (this.isPlayerMode() != other.isPlayerMode()) {
            return false;
         }

         if (Float.compare(this.getStableAngle(), other.getStableAngle()) != 0) {
            return false;
         }

         if (this.getRotationDelay() != other.getRotationDelay()) {
            return false;
         }

         if (this.getRotationDuration() != other.getRotationDuration()) {
            return false;
         }

         if (this.isCanSteer() != other.isCanSteer()) {
            return false;
         }

         if (this.isCanRide() != other.isCanRide()) {
            return false;
         }

         if (this.isBaseEntityVisible() != other.isBaseEntityVisible()) {
            return false;
         }

         if (this.isModelRotationLock() != other.isModelRotationLock()) {
            return false;
         }

         Object this$stepHeight = this.getStepHeight();
         Object other$stepHeight = other.getStepHeight();
         if (this$stepHeight == null ? other$stepHeight == null : this$stepHeight.equals(other$stepHeight)) {
            Object this$mountModelId = this.getMountModelId();
            Object other$mountModelId = other.getMountModelId();
            if (this$mountModelId == null ? other$mountModelId == null : this$mountModelId.equals(other$mountModelId)) {
               Object this$mountDriverBone = this.getMountDriverBone();
               Object other$mountDriverBone = other.getMountDriverBone();
               if (this$mountDriverBone == null ? other$mountDriverBone == null : this$mountDriverBone.equals(other$mountDriverBone)) {
                  Object this$hitbox = this.getHitbox();
                  Object other$hitbox = other.getHitbox();
                  if (this$hitbox == null ? other$hitbox == null : this$hitbox.equals(other$hitbox)) {
                     Object this$models = this.getModels();
                     Object other$models = other.getModels();
                     return this$models == null ? other$models == null : this$models.equals(other$models);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof V3Data;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getRenderDistance();
      result = result * 59 + (this.isHeadClampUneven() ? 79 : 97);
      result = result * 59 + (this.isBodyClampUneven() ? 79 : 97);
      result = result * 59 + Float.floatToIntBits(this.getMaxHeadAngle());
      result = result * 59 + Float.floatToIntBits(this.getMaxBodyAngle());
      result = result * 59 + Float.floatToIntBits(this.getMinHeadAngle());
      result = result * 59 + Float.floatToIntBits(this.getMinBodyAngle());
      result = result * 59 + (this.isPlayerMode() ? 79 : 97);
      result = result * 59 + Float.floatToIntBits(this.getStableAngle());
      result = result * 59 + this.getRotationDelay();
      result = result * 59 + this.getRotationDuration();
      result = result * 59 + (this.isCanSteer() ? 79 : 97);
      result = result * 59 + (this.isCanRide() ? 79 : 97);
      result = result * 59 + (this.isBaseEntityVisible() ? 79 : 97);
      result = result * 59 + (this.isModelRotationLock() ? 79 : 97);
      Object $stepHeight = this.getStepHeight();
      result = result * 59 + ($stepHeight == null ? 43 : $stepHeight.hashCode());
      Object $mountModelId = this.getMountModelId();
      result = result * 59 + ($mountModelId == null ? 43 : $mountModelId.hashCode());
      Object $mountDriverBone = this.getMountDriverBone();
      result = result * 59 + ($mountDriverBone == null ? 43 : $mountDriverBone.hashCode());
      Object $hitbox = this.getHitbox();
      result = result * 59 + ($hitbox == null ? 43 : $hitbox.hashCode());
      Object $models = this.getModels();
      return result * 59 + ($models == null ? 43 : $models.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "V3Data(renderDistance="
         + this.getRenderDistance()
         + ", isHeadClampUneven="
         + this.isHeadClampUneven()
         + ", isBodyClampUneven="
         + this.isBodyClampUneven()
         + ", maxHeadAngle="
         + this.getMaxHeadAngle()
         + ", maxBodyAngle="
         + this.getMaxBodyAngle()
         + ", minHeadAngle="
         + this.getMinHeadAngle()
         + ", minBodyAngle="
         + this.getMinBodyAngle()
         + ", playerMode="
         + this.isPlayerMode()
         + ", stableAngle="
         + this.getStableAngle()
         + ", rotationDelay="
         + this.getRotationDelay()
         + ", rotationDuration="
         + this.getRotationDuration()
         + ", canSteer="
         + this.isCanSteer()
         + ", canRide="
         + this.isCanRide()
         + ", mountModelId="
         + this.getMountModelId()
         + ", mountDriverBone="
         + this.getMountDriverBone()
         + ", isBaseEntityVisible="
         + this.isBaseEntityVisible()
         + ", modelRotationLock="
         + this.isModelRotationLock()
         + ", stepHeight="
         + this.getStepHeight()
         + ", hitbox="
         + this.getHitbox()
         + ", models="
         + this.getModels()
         + ")";
   }

   public static class ModelData {
      private final Map<String, V3Data.StateData> states = Maps.newConcurrentMap();
      private final Map<ModelState, V3Data.StateProperty> defaultStates = Maps.newConcurrentMap();
      private boolean canHurt;
      private boolean lockPitch;
      private boolean lockYaw;

      @Generated
      public Map<String, V3Data.StateData> getStates() {
         return this.states;
      }

      @Generated
      public Map<ModelState, V3Data.StateProperty> getDefaultStates() {
         return this.defaultStates;
      }

      @Generated
      public boolean isCanHurt() {
         return this.canHurt;
      }

      @Generated
      public boolean isLockPitch() {
         return this.lockPitch;
      }

      @Generated
      public boolean isLockYaw() {
         return this.lockYaw;
      }

      @Generated
      public void setCanHurt(boolean canHurt) {
         this.canHurt = canHurt;
      }

      @Generated
      public void setLockPitch(boolean lockPitch) {
         this.lockPitch = lockPitch;
      }

      @Generated
      public void setLockYaw(boolean lockYaw) {
         this.lockYaw = lockYaw;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof V3Data.ModelData other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.isCanHurt() != other.isCanHurt()) {
            return false;
         } else if (this.isLockPitch() != other.isLockPitch()) {
            return false;
         } else if (this.isLockYaw() != other.isLockYaw()) {
            return false;
         } else {
            Object this$states = this.getStates();
            Object other$states = other.getStates();
            if (this$states == null ? other$states == null : this$states.equals(other$states)) {
               Object this$defaultStates = this.getDefaultStates();
               Object other$defaultStates = other.getDefaultStates();
               return this$defaultStates == null ? other$defaultStates == null : this$defaultStates.equals(other$defaultStates);
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof V3Data.ModelData;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + (this.isCanHurt() ? 79 : 97);
         result = result * 59 + (this.isLockPitch() ? 79 : 97);
         result = result * 59 + (this.isLockYaw() ? 79 : 97);
         Object $states = this.getStates();
         result = result * 59 + ($states == null ? 43 : $states.hashCode());
         Object $defaultStates = this.getDefaultStates();
         return result * 59 + ($defaultStates == null ? 43 : $defaultStates.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "V3Data.ModelData(states="
            + this.getStates()
            + ", defaultStates="
            + this.getDefaultStates()
            + ", canHurt="
            + this.isCanHurt()
            + ", lockPitch="
            + this.isLockPitch()
            + ", lockYaw="
            + this.isLockYaw()
            + ")";
      }
   }

   public static class StateData {
      private double lerpIn;
      private double lerpOut;
      private double time;
      private double speed;
      private IAnimationProperty.Phase phase;
      private BlueprintAnimation.LoopMode forceLoopMode;
      private boolean forceOverride;

      @Generated
      public double getLerpIn() {
         return this.lerpIn;
      }

      @Generated
      public double getLerpOut() {
         return this.lerpOut;
      }

      @Generated
      public double getTime() {
         return this.time;
      }

      @Generated
      public double getSpeed() {
         return this.speed;
      }

      @Generated
      public IAnimationProperty.Phase getPhase() {
         return this.phase;
      }

      @Generated
      public BlueprintAnimation.LoopMode getForceLoopMode() {
         return this.forceLoopMode;
      }

      @Generated
      public boolean isForceOverride() {
         return this.forceOverride;
      }

      @Generated
      public void setLerpIn(double lerpIn) {
         this.lerpIn = lerpIn;
      }

      @Generated
      public void setLerpOut(double lerpOut) {
         this.lerpOut = lerpOut;
      }

      @Generated
      public void setTime(double time) {
         this.time = time;
      }

      @Generated
      public void setSpeed(double speed) {
         this.speed = speed;
      }

      @Generated
      public void setPhase(IAnimationProperty.Phase phase) {
         this.phase = phase;
      }

      @Generated
      public void setForceLoopMode(BlueprintAnimation.LoopMode forceLoopMode) {
         this.forceLoopMode = forceLoopMode;
      }

      @Generated
      public void setForceOverride(boolean forceOverride) {
         this.forceOverride = forceOverride;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof V3Data.StateData other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (Double.compare(this.getLerpIn(), other.getLerpIn()) != 0) {
            return false;
         } else if (Double.compare(this.getLerpOut(), other.getLerpOut()) != 0) {
            return false;
         } else if (Double.compare(this.getTime(), other.getTime()) != 0) {
            return false;
         } else if (Double.compare(this.getSpeed(), other.getSpeed()) != 0) {
            return false;
         } else if (this.isForceOverride() != other.isForceOverride()) {
            return false;
         } else {
            Object this$phase = this.getPhase();
            Object other$phase = other.getPhase();
            if (this$phase == null ? other$phase == null : this$phase.equals(other$phase)) {
               Object this$forceLoopMode = this.getForceLoopMode();
               Object other$forceLoopMode = other.getForceLoopMode();
               return this$forceLoopMode == null ? other$forceLoopMode == null : this$forceLoopMode.equals(other$forceLoopMode);
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof V3Data.StateData;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         long $lerpIn = Double.doubleToLongBits(this.getLerpIn());
         result = result * 59 + (int)($lerpIn >>> 32 ^ $lerpIn);
         long $lerpOut = Double.doubleToLongBits(this.getLerpOut());
         result = result * 59 + (int)($lerpOut >>> 32 ^ $lerpOut);
         long $time = Double.doubleToLongBits(this.getTime());
         result = result * 59 + (int)($time >>> 32 ^ $time);
         long $speed = Double.doubleToLongBits(this.getSpeed());
         result = result * 59 + (int)($speed >>> 32 ^ $speed);
         result = result * 59 + (this.isForceOverride() ? 79 : 97);
         Object $phase = this.getPhase();
         result = result * 59 + ($phase == null ? 43 : $phase.hashCode());
         Object $forceLoopMode = this.getForceLoopMode();
         return result * 59 + ($forceLoopMode == null ? 43 : $forceLoopMode.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "V3Data.StateData(lerpIn="
            + this.getLerpIn()
            + ", lerpOut="
            + this.getLerpOut()
            + ", time="
            + this.getTime()
            + ", speed="
            + this.getSpeed()
            + ", phase="
            + this.getPhase()
            + ", forceLoopMode="
            + this.getForceLoopMode()
            + ", forceOverride="
            + this.isForceOverride()
            + ")";
      }
   }

   public static class StateProperty {
      private String stateId;
      private double lerpIn;
      private double lerpOut;
      private double speed;

      @Generated
      public String getStateId() {
         return this.stateId;
      }

      @Generated
      public double getLerpIn() {
         return this.lerpIn;
      }

      @Generated
      public double getLerpOut() {
         return this.lerpOut;
      }

      @Generated
      public double getSpeed() {
         return this.speed;
      }

      @Generated
      public void setStateId(String stateId) {
         this.stateId = stateId;
      }

      @Generated
      public void setLerpIn(double lerpIn) {
         this.lerpIn = lerpIn;
      }

      @Generated
      public void setLerpOut(double lerpOut) {
         this.lerpOut = lerpOut;
      }

      @Generated
      public void setSpeed(double speed) {
         this.speed = speed;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof V3Data.StateProperty other)) {
            return false;
         } else {
            if (!other.canEqual(this)) {
               return false;
            }

            if (Double.compare(this.getLerpIn(), other.getLerpIn()) != 0) {
               return false;
            }

            if (Double.compare(this.getLerpOut(), other.getLerpOut()) != 0) {
               return false;
            }

            if (Double.compare(this.getSpeed(), other.getSpeed()) != 0) {
               return false;
            }

            Object this$stateId = this.getStateId();
            Object other$stateId = other.getStateId();
            return this$stateId == null ? other$stateId == null : this$stateId.equals(other$stateId);
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof V3Data.StateProperty;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         long $lerpIn = Double.doubleToLongBits(this.getLerpIn());
         result = result * 59 + (int)($lerpIn >>> 32 ^ $lerpIn);
         long $lerpOut = Double.doubleToLongBits(this.getLerpOut());
         result = result * 59 + (int)($lerpOut >>> 32 ^ $lerpOut);
         long $speed = Double.doubleToLongBits(this.getSpeed());
         result = result * 59 + (int)($speed >>> 32 ^ $speed);
         Object $stateId = this.getStateId();
         return result * 59 + ($stateId == null ? 43 : $stateId.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "V3Data.StateProperty(stateId="
            + this.getStateId()
            + ", lerpIn="
            + this.getLerpIn()
            + ", lerpOut="
            + this.getLerpOut()
            + ", speed="
            + this.getSpeed()
            + ")";
      }
   }
}
