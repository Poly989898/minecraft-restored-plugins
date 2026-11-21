package com.ticxo.modelengine.api.animation.interpolator;

import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class PrePostInterpolator<IN extends AbstractKeyframe<OUT>, OUT> extends KeyframeInterpolator<IN, OUT> {
   private final BiFunction<OUT, OUT, OUT> derivativeFunc;
   private final BiConsumer<KeyframeInterpolator.Context<IN, OUT>, IN> finalizerFunc;

   public PrePostInterpolator(
      KeyframeInterpolator.Interpolation<IN, OUT> interpolateFunc,
      BiConsumer<KeyframeInterpolator.Context<IN, OUT>, IN> finalizerFunc,
      BiFunction<OUT, OUT, OUT> derivativeFunc
   ) {
      this(interpolateFunc, finalizerFunc, derivativeFunc, () -> null);
   }

   public PrePostInterpolator(
      KeyframeInterpolator.Interpolation<IN, OUT> interpolateFunc,
      BiConsumer<KeyframeInterpolator.Context<IN, OUT>, IN> finalizerFunc,
      BiFunction<OUT, OUT, OUT> derivativeFunc,
      Supplier<OUT> def
   ) {
      this.setInterpolateFunc(interpolateFunc);
      this.derivativeFunc = derivativeFunc;
      this.finalizerFunc = finalizerFunc;
      this.setDefaultValue(def);
   }

   @Nullable
   @Override
   public OUT interpolate(ModelBone bone, IAnimationProperty property) {
      return this.interpolate(bone, property, (float)property.getTime());
   }

   @Override
   public OUT interpolateAndDerive(ModelBone bone, IAnimationProperty property) {
      float time = (float)property.getTime();
      float lastTime = (float)TMath.clamp(property.getLastTime(), 0.0, property.getTime());
      OUT curr = this.interpolate(bone, property, time);
      if (TMath.isSimilar(time, lastTime)) {
         return this.derivativeFunc.apply(curr, curr);
      }

      OUT prev = this.interpolate(bone, property, lastTime);
      return this.derivativeFunc.apply(prev, curr);
   }

   @Nullable
   private OUT interpolate(ModelBone bone, IAnimationProperty property, float time) {
      if (this.isEmpty()) {
         return this.defaultValue.get();
      }

      Entry<Float, IN> lastEntry = this.floorEntry(time);
      if (lastEntry != null && Float.compare(lastEntry.getKey(), time) == 0) {
         IN exact = lastEntry.getValue();
         this.finalizerFunc.accept(new KeyframeInterpolator.Context<>(time, time, exact, exact, property, bone, this), exact);
         return exact.getValue(0, property);
      }

      Entry<Float, IN> nextEntry = this.higherEntry(time);
      if (nextEntry == null) {
         nextEntry = this.lastEntry();
      }

      if (lastEntry == null) {
         lastEntry = this.firstEntry();
      }

      float nextKey = nextEntry.getKey();
      float lastKey = lastEntry.getKey();
      IN nextFrame = nextEntry.getValue();
      IN prevFrame = lastEntry.getValue();
      if (nextKey == lastKey) {
         this.finalizerFunc.accept(new KeyframeInterpolator.Context<>(lastKey, nextKey, prevFrame, nextFrame, property, bone, this), prevFrame);
         return prevFrame.getValue(0, property);
      } else {
         float t = (time - lastKey) / (nextKey - lastKey);
         OUT next = nextFrame.getValue(0, property);
         OUT prev = prevFrame.getValue(1, property);
         return this.interpolateFunc
            .interpolate(new KeyframeInterpolator.Context<>(lastKey, nextKey, prevFrame, nextFrame, property, bone, this), prev, next, t);
      }
   }
}
