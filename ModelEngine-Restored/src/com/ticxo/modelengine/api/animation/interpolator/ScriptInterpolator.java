package com.ticxo.modelengine.api.animation.interpolator;

import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class ScriptInterpolator<IN extends AbstractKeyframe<OUT>, OUT> extends KeyframeInterpolator<IN, OUT> {
   private final Supplier<OUT> supplier;
   private final BiConsumer<OUT, OUT> combiner;

   @Nullable
   @Override
   public OUT interpolate(ModelBone bone, IAnimationProperty property) {
      if (this.isEmpty()) {
         return null;
      }

      OUT combined = this.supplier.get();
      float lastTime = (float)property.getLastTime();
      float currTime = (float)property.getTime();
      float frame = lastTime;
      float lastFrame = this.lastKey();
      if (lastTime > currTime) {
         while (frame < lastFrame && (frame = this.getHigherKey(frame)) <= lastFrame) {
            OUT data = this.get(frame).getValue(0, property);
            this.combiner.accept(combined, data);
         }

         frame = -1.0F;
      }

      while (frame < lastFrame && (frame = this.getHigherKey(frame)) <= currTime) {
         OUT data = this.get(frame).getValue(0, property);
         this.combiner.accept(combined, data);
      }

      return combined;
   }
}
