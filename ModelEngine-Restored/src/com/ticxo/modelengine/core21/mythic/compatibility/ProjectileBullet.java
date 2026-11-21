package com.ticxo.modelengine.core21.mythic.compatibility;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.entity.data.DummyEntityData;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.Segment;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport.MEGProjectile;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport.MEGProjectileData;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.UUID;
import java.util.Map.Entry;
import org.bukkit.Color;

public class ProjectileBullet implements MEGProjectile {
   private Dummy<?> entity = new Dummy();
   private ModelBlueprint modelBlueprint;
   private ActiveModel activeModel;
   private ModeledEntity modeledEntity;

   public ProjectileBullet(MEGProjectileData modelData, AbstractLocation location) {
      String modelId = modelData.getModelId();
      String state = modelData.getState();
      double scale = modelData.getScale();
      String color = modelData.getColor();
      boolean enchanted = modelData.isEnchanted();
      boolean glowing = modelData.isGlowing();
      String glowColor = modelData.getGlowColor();
      Integer brightness = modelData.getBrightness();
      this.entity.setLocation(BukkitAdapter.adapt(location));
      this.modelBlueprint = ModelEngineAPI.getBlueprint(modelId);
      if (this.modelBlueprint == null) {
         MythicLogger.error("Failed to spawn Projectile Bullet: Model " + modelId + " not found");
      } else {
         this.modeledEntity = ModelEngineAPI.createModeledEntity(this.entity);
         this.activeModel = ModelEngineAPI.createActiveModel(this.modelBlueprint);
         this.activeModel.setScale(scale);
         this.activeModel.setHitboxScale(0.0);
         if (color != null) {
            if (color.startsWith("#")) {
               color = color.substring(1);
            }

            this.activeModel.setDefaultTint(Color.fromRGB(Integer.parseInt(color, 16)));
         }

         if (glowing) {
            this.activeModel.setGlowing(true);
            if (glowColor != null) {
               if (glowColor.startsWith("#")) {
                  glowColor = glowColor.substring(1);
               }

               this.activeModel.setGlowColor(Integer.parseInt(glowColor, 16));
            }
         }

         if (brightness != null) {
            this.activeModel.setBlockLight(brightness);
            this.activeModel.setSkyLight(brightness);
         }

         this.modeledEntity.getAnimationLodHandler().setEnabled(modelData.isUseALOD());
         this.modeledEntity.addModel(this.activeModel, true);
         BodyRotationController rotationController = this.modeledEntity.getBase().getBodyRotationController();
         rotationController.setMaxHeadAngle(0.0F);
         rotationController.setMinHeadAngle(0.0F);
         rotationController.setMaxBodyAngle(0.0F);
         rotationController.setMinBodyAngle(0.0F);
         rotationController.setRotationDelay(0);
         rotationController.setRotationDuration(0);

         for (Entry<String, ModelBone> entry : this.activeModel.getBones().entrySet()) {
            ModelBone bone = entry.getValue();
            if (enchanted) {
               bone.setEnchanted(true);
            }

            bone.getBoneBehavior(BoneBehaviorTypes.SEGMENT).ifPresent(segment -> ((Segment)segment).setBounded(false));
         }

         if (state != null) {
            AnimationHandler handler = this.activeModel.getAnimationHandler();
            if (handler instanceof IStateMachineHandler stateMachineHandler) {
               IAnimationProperty property = stateMachineHandler.playAnimation(100, state, 0.0, 0.0, 1.0, true);
               if (property != null) {
                  property.setForceLoopMode(BlueprintAnimation.LoopMode.LOOP);
               }
            } else {
               IAnimationProperty property = handler.playAnimation(state, 0.0, 0.0, 1.0, true);
               if (property != null) {
                  property.setForceLoopMode(BlueprintAnimation.LoopMode.LOOP);
               }
            }
         }
      }
   }

   public UUID getEntityId() {
      return this.entity.getUUID();
   }

   public void setLocation(AbstractLocation location) {
      this.entity.setLocation(BukkitAdapter.adapt(location));
   }

   public void setDirection(AbstractVector direction) {
      double yaw = Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
      double pitch = -Math.toDegrees(Math.asin(direction.getY() / direction.length()));
      this.entity.setXHeadRot((float)pitch);
      this.entity.setYHeadRot((float)yaw);
      this.entity.setYBodyRot((float)yaw);
   }

   public void setRenderRadius(int radius) {
      DummyEntityData<?> data = this.entity.getData();
      data.setRenderRadius(radius);
   }

   public void disableCulling() {
      DummyEntityData<?> data = this.entity.getData();
      data.setVerticalCull(false);
      data.setBackCull(false);
      data.setBlockedCull(false);
      data.cullUpdate();
   }

   public void close() throws Exception {
      BlueprintAnimation animation = this.modelBlueprint.getAnimations().get("death");
      if (animation == null) {
         ModelEngineAPI.removeModeledEntity(this.entity.getUUID());
         this.entity.setRemoved(true);
      } else {
         SimpleProperty property = new SimpleProperty(this.activeModel, animation, 0.0, 0.0, 1.0);
         property.setForceLoopMode(BlueprintAnimation.LoopMode.ONCE);
         property.setForceOverride(BlueprintAnimation.OverrideMode.OVERRIDE);
         property.setOnEndTask(() -> {
            ModelEngineAPI.removeModeledEntity(this.entity.getUUID());
            this.entity.setRemoved(true);
         });
         if (!this.activeModel.getAnimationHandler().playAnimation(property, true)) {
            ModelEngineAPI.removeModeledEntity(this.entity.getUUID());
            this.entity.setRemoved(true);
         }
      }
   }
}
