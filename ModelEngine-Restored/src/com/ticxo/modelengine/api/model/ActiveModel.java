package com.ticxo.modelengine.api.model;

import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.LeashManager;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.type.Leash;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.meta.IMetaHolder;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

public interface ActiveModel extends DataIO, IMetaHolder {
   /** Returns the modeled entity this model is attached to. */
   ModeledEntity getModeledEntity();

   void setModeledEntity(ModeledEntity modeledEntity);

   /** Returns the blueprint this model was created from. */
   ModelBlueprint getBlueprint();

   ModelRenderer getModelRenderer();

   AnimationHandler getAnimationHandler();

   Optional<PivotOverride> getPivotOverride();

   void setPivotOverride(PivotOverride override);

   /** Returns the bones of this model by bone id. */
   Map<String, ModelBone> getBones();

   Map<BoneBehaviorType<?>, BehaviorManager<?>> getBehaviorManagers();

   Map<BoneBehaviorType<?>, BehaviorRenderer> getBehaviorRenderers();

   boolean isMainHitbox();

   void setMainHitbox(boolean mainHitbox);

   Vector3fc getScale();

   void setScale(double scale);

   void setScale(Vector3fc scale);

   Vector3fc getHitboxScale();

   void setHitboxScale(double scale);

   void setHitboxScale(Vector3fc scale);

   /**
    * Advances the model by one tick (animations, bone behaviors, bone updates). Does nothing once the model is destroyed;
    * the animation and bone work only runs while the base entity has tracking data.
    */
   void tick();

   /**
    * Destroys all bones, behavior managers and the renderer, clears the bone map and marks the model destroyed.
    * {@link #tick()} does nothing afterwards.
    */
   void destroy();

   /** Returns true once {@link #destroy()} has been called. */
   boolean isDestroyed();

   boolean isRemoved();

   void setRemoved(boolean removed);

   void setAutoRendererInitialization(boolean autoRendererInitialization);

   boolean isHitboxVisible();

   void setHitboxVisible(boolean hitboxVisible);

   boolean isShadowVisible();

   void setShadowVisible(boolean shadowVisible);

   float getViewRange();

   void setViewRange(float viewRange);

   void initializeRenderer();

   void generateModel();

   void forceGenerateBone(String parentId, String prefix, BlueprintBone blueprintBone);

   void removeBone(String bone);

   Callback<ActiveModel.Scale> getScaleCallback();

   Callback<Runnable> getPreSendCallback();

   Callback<Runnable> getPostSendCallback();

   void setCanHurt(boolean canHurt);

   boolean canHurt();

   Color getDefaultTint();

   void setDefaultTint(Color defaultTint);

   Color getDamageTint();

   void setDamageTint(Color damageTint);

   boolean wasMarkedHurt();

   boolean isMarkedHurt();

   boolean isGlowing();

   void setGlowing(@Nullable Boolean glowing);

   int getGlowColor();

   void setGlowColor(@Nullable Integer glowColor);

   @Nullable
   Set<UUID> getGlowAudience();

   void setGlowAudience(@Nullable Set<UUID> glowAudience);

   int getBlockLight();

   void setBlockLight(int blockLight);

   int getSkyLight();

   void setSkyLight(int skyLight);

   Billboard getBillboard();

   void setBillboard(Billboard billboard);

   boolean isOnFire();

   void setOnFire(Boolean onFire);

   boolean canRenderFire();

   void setRenderFire(Boolean renderFire);

   float getXHeadRot();

   float getYHeadRot();

   float getYBodyRot();

   boolean isLockPitch();

   void setLockPitch(boolean lockPitch);

   boolean isLockYaw();

   void setLockYaw(boolean lockYaw);

   void setLockedYHeadRot(float lockedYHeadRot);

   float getLockedYHeadRot();

   void setLockedXHeadRot(float lockedXHeadRot);

   float getLockedXHeadRot();

   void setLockedYBodyRot(float lockedYBodyRot);

   float getLockedYBodyRot();

   boolean isModelRotationLocked();

   void setModelRotationLocked(Boolean modelRotationLocked);

   boolean isInvisUpdate();

   void setInvisUpdate(boolean invisUpdate);

   boolean isCameraPivot();

   void setCameraPivot(boolean cameraPivot);

   default Optional<ModelBone> getBone(String boneId) {
      return Optional.ofNullable(this.getBones().get(boneId));
   }

   <T extends BoneBehavior> Optional<BehaviorManager<T>> getBehaviorManager(BoneBehaviorType<T> type);

   Optional<BehaviorRenderer> getBehaviorRenderer(BoneBehaviorType<?> type);

   default <T extends MountManager & BehaviorManager<? extends Mount>> Optional<T> getMountManager() {
      Optional maybe = this.getBehaviorManager((BoneBehaviorType)BoneBehaviorTypes.MOUNT);
      return maybe.map(behaviorManager -> (T)behaviorManager);
   }

   default <T extends LeashManager & BehaviorManager<? extends Leash>> Optional<T> getLeashManager() {
      Optional maybe = this.getBehaviorManager((BoneBehaviorType)BoneBehaviorTypes.LEASH);
      return maybe.map(behaviorManager -> (T)behaviorManager);
   }

   @FunctionalInterface
   interface Scale {
      void onScale(ActiveModel activeModel, double value);
   }
}
