package com.ticxo.modelengine.api.animation.handler;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.utils.MiscUtils;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.Nullable;

public interface IPriorityHandler extends AnimationHandler {
   void forEachProperty(BiConsumer<String, IAnimationProperty> consumer);

   void playState(ModelState state);

   @Nullable
   default IAnimationProperty playAnimation(ModelBlueprint source, String animation, double lerpIn, double lerpOut, double speed, boolean force) {
      BlueprintAnimation blueprintAnimation = source.getAnimations().get(animation);
      if (blueprintAnimation == null) {
         return null;
      }

      SimpleProperty property = new SimpleProperty(this.getActiveModel(), blueprintAnimation, lerpIn, lerpOut, speed);
      return this.playAnimation(property, force) ? property : null;
   }

   default void stopAnimation(ModelBlueprint source, String animation) {
      this.stopAnimation(this.resolveAnimationName(source, animation));
   }

   default void forceStopAnimation(ModelBlueprint source, String animation) {
      this.forceStopAnimation(this.resolveAnimationName(source, animation));
   }

   private String resolveAnimationName(ModelBlueprint source, String animation) {
      return source == this.getActiveModel().getBlueprint() ? animation : MiscUtils.createAnimationRef(source.getName(), animation);
   }

   @Override
   default String getId() {
      return "priority";
   }
}
