package com.ticxo.modelengine.api.animation.handler;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import java.util.Map;
import java.util.function.UnaryOperator;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

/**
 * Plays and mixes the animations of one {@link ActiveModel}.
 *
 * Implementations decide how running animations are combined on each bone, for example by priority
 * ({@code IPriorityHandler}) or through a state machine ({@code IStateMachineHandler}).
 */
public interface AnimationHandler extends DataIO {
   /** The model this handler animates. */
   ActiveModel getActiveModel();

   /** Per-tick step before bones are updated: advances every running animation and removes those that have ended. */
   void prepare();

   /** Applies the running animations to the bone for this tick. */
   void updateBone(ModelBone bone);

   /**
    * Same as {@link #updateBone(ModelBone)}, but each running property may be replaced by the mapper first.
    * The default implementation ignores the mapper.
    */
   default void updateBone(ModelBone bone, @Nullable UnaryOperator<IAnimationProperty> propertyMapper) {
      this.updateBone(bone);
   }

   /** Whether every running animation has finished (used to keep a dead entity's model alive until its death animation ends). */
   boolean hasFinishedAllAnimations();

   /** Sets the animation and timing used when the model enters the property's state. */
   void setDefaultProperty(AnimationHandler.DefaultProperty defaultProperty);

   /** The default animation settings for a model state, or null if none is set. */
   AnimationHandler.DefaultProperty getDefaultProperty(ModelState state);

   /** Per-tick step for global keyframe types, which act on the whole model rather than a bone (only the script keyframe is global). */
   void tickGlobal();

   /**
    * Starts an animation from the model's blueprint.
    *
    * @param animation animation name in the blueprint
    * @param lerpIn seconds to blend in
    * @param lerpOut seconds to blend out when it stops
    * @param speed playback speed multiplier
    * @param force replace an animation of the same name even if it cannot be replaced yet
    * @return the new property, or null if the blueprint has no such animation or the start was refused (cancelled event, or not replaceable and not forced)
    */
   @Nullable
   IAnimationProperty playAnimation(String animation, double lerpIn, double lerpOut, double speed, boolean force);

   /**
    * Starts an animation from a prepared property. Fires {@code AnimationPlayEvent}, which can cancel it.
    *
    * @param force replace a running animation of the same name even if it cannot be replaced yet
    * @return true if the property is now running
    */
   boolean playAnimation(IAnimationProperty property, boolean force);

   /** Whether an animation with this name is currently running (including while fading out). */
   boolean isPlayingAnimation(String animation);

   /** Stops an animation, letting it blend out over its lerp-out time; with no lerp-out it stops immediately. */
   void stopAnimation(String animation);

   /** Stops an animation immediately without blending out. Fires {@code AnimationEndEvent}. */
   void forceStopAnimation(String animation);

   /** Immediately stops every running animation, firing {@code AnimationEndEvent} for each. */
   void forceStopAllAnimations();

   /** The running property for this animation name, or null. */
   @Nullable
   IAnimationProperty getAnimation(String animation);

   /** All running animations by name. */
   Map<String, IAnimationProperty> getAnimations();

   /** Identifier of this handler type, saved with the model so the same handler can be restored. */
   String getId();

   /** Whether the base entity is walking and no root motion animation is currently queued. */
   default boolean isWalking(ActiveModel activeModel) {
      ModeledEntity modeledEntity = activeModel.getModeledEntity();
      BaseEntity<?> baseEntity = modeledEntity.getBase();
      return !modeledEntity.getRootMotionHandler().isQueued() && baseEntity.isWalking();
   }

   @Override
   default void save(SavedData data) {
      SavedData defaultData = new SavedData();

      for (ModelState state : ModelState.values()) {
         AnimationHandler.DefaultProperty property = this.getDefaultProperty(state);
         SavedData propertyData = new SavedData();
         propertyData.putDouble("lerp_in", property.lerpIn);
         propertyData.putDouble("lerp_out", property.lerpOut);
         propertyData.putDouble("speed", property.speed);
         defaultData.putData(state.name(), propertyData);
      }

      data.putData("defaults", defaultData);
      data.putString("id", this.getId());
   }

   @Override
   default void load(SavedData data) {
      data.getData("defaults")
         .ifPresent(
            defaultData -> {
               for (String key : defaultData.keySet()) {
                  ModelState state = ModelState.get(key);
                  defaultData.getData(key)
                     .ifPresent(
                        propertyData -> this.setDefaultProperty(
                           new AnimationHandler.DefaultProperty(
                              state, propertyData.getDouble("lerp_in"), propertyData.getDouble("lerp_out"), propertyData.getDouble("speed")
                           )
                        )
                     );
               }
            }
         );
   }

   @Getter
   @ToString
   class DefaultProperty {
      private final ModelState state;
      private final String animation;
      private final double lerpIn;
      private final double lerpOut;
      private final double speed;
      private final boolean merge;

      public DefaultProperty(ModelState state, double lerpIn, double lerpOut, double speed) {
         this(state, state.getString(), lerpIn, lerpOut, speed, false);
      }

      public DefaultProperty(ModelState state, double lerpIn, double lerpOut, double speed, boolean merge) {
         this(state, state.getString(), lerpIn, lerpOut, speed, merge);
      }

      public DefaultProperty(ModelState state, String animation, double lerpIn, double lerpOut, double speed) {
         this(state, animation, lerpIn, lerpOut, speed, false);
      }

      public DefaultProperty(ModelState state, String animation, double lerpIn, double lerpOut, double speed, boolean merge) {
         this.state = state;
         this.animation = animation;
         this.lerpIn = lerpIn;
         this.lerpOut = lerpOut;
         this.speed = speed;
         this.merge = merge;
      }

      public IAnimationProperty build(ActiveModel model) {
         return this.build(model, this.lerpIn, this.lerpOut, this.speed);
      }

      public IAnimationProperty build(ActiveModel model, double lerpIn, double lerpOut, double speed) {
         BlueprintAnimation blueprintAnimation = model.getBlueprint().getAnimations().get(this.animation);
         if (blueprintAnimation == null) {
            return null;
         }

         SimpleProperty property = new SimpleProperty(model, blueprintAnimation, lerpIn, lerpOut, speed);
         property.setSkipLastFrame(true);
         return property;
      }

      public IAnimationProperty buildRef(ActiveModel model) {
         return this.buildRef(model, this.lerpIn, this.lerpOut, this.speed);
      }

      public IAnimationProperty buildRef(ActiveModel model, double lerpIn, double lerpOut, double speed) {
         BlueprintAnimation blueprintAnimation = model.getBlueprint().getAnimationOrRef(this.animation);
         if (blueprintAnimation == null) {
            return null;
         }

         SimpleProperty property = new SimpleProperty(model, blueprintAnimation, lerpIn, lerpOut, speed);
         property.setSkipLastFrame(true);
         return property;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof AnimationHandler.DefaultProperty other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (Double.compare(this.getLerpIn(), other.getLerpIn()) != 0) {
            return false;
         } else if (Double.compare(this.getLerpOut(), other.getLerpOut()) != 0) {
            return false;
         } else if (Double.compare(this.getSpeed(), other.getSpeed()) != 0) {
            return false;
         } else if (this.isMerge() != other.isMerge()) {
            return false;
         } else {
            Object this$state = this.getState();
            Object other$state = other.getState();
            if (this$state == null ? other$state == null : this$state.equals(other$state)) {
               Object this$animation = this.getAnimation();
               Object other$animation = other.getAnimation();
               return this$animation == null ? other$animation == null : this$animation.equals(other$animation);
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof AnimationHandler.DefaultProperty;
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
         result = result * 59 + (this.isMerge() ? 79 : 97);
         Object $state = this.getState();
         result = result * 59 + ($state == null ? 43 : $state.hashCode());
         Object $animation = this.getAnimation();
         return result * 59 + ($animation == null ? 43 : $animation.hashCode());
      }
   }
}
