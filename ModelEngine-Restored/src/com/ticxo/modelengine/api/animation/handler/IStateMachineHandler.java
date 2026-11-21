package com.ticxo.modelengine.api.animation.handler;

import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import org.jetbrains.annotations.Nullable;

public interface IStateMachineHandler extends AnimationHandler {
   ModelBlueprint getBlueprint();

   @Nullable
   default IAnimationProperty getAnimation(int priority, String animation) {
      return this.getAnimation(priority, this.getBlueprint(), animation);
   }

   @Nullable
   IAnimationProperty getAnimation(int priority, ModelBlueprint source, String animation);

   @Nullable
   default IAnimationProperty playAnimation(int priority, String animation, double lerpIn, double lerpOut, double speed, boolean force) {
      return this.playAnimation(priority, this.getBlueprint(), animation, lerpIn, lerpOut, speed, force);
   }

   @Nullable
   IAnimationProperty playAnimation(int priority, ModelBlueprint source, String animation, double lerpIn, double lerpOut, double speed, boolean force);

   boolean playAnimation(int priority, IAnimationProperty property, boolean force);

   void refreshState(AnimationHandler.DefaultProperty property);

   default boolean isPlayingAnimation(int priority, String animation) {
      return this.isPlayingAnimation(priority, this.getBlueprint(), animation);
   }

   boolean isPlayingAnimation(int priority, ModelBlueprint source, String animation);

   default void stopAnimation(int priority, String animation) {
      this.stopAnimation(priority, this.getBlueprint(), animation);
   }

   void stopAnimation(int priority, ModelBlueprint source, String animation);

   default void forceStopAnimation(int priority, String animation) {
      this.forceStopAnimation(priority, this.getBlueprint(), animation);
   }

   void forceStopAnimation(int priority, ModelBlueprint source, String animation);

   @Override
   default String getId() {
      return "state_machine";
   }
}
