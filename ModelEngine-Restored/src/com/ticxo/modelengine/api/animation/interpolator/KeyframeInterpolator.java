package com.ticxo.modelengine.api.animation.interpolator;

import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

public class KeyframeInterpolator<IN extends AbstractKeyframe<OUT>, OUT> extends TreeMap<Float, IN> {
   protected KeyframeInterpolator.Interpolation<IN, OUT> interpolateFunc;
   protected Supplier<OUT> defaultValue = null;

   public KeyframeInterpolator<IN, OUT> setInterpolateFunc(KeyframeInterpolator.Interpolation<IN, OUT> interpolateFunc) {
      this.interpolateFunc = interpolateFunc;
      return this;
   }

   public KeyframeInterpolator<IN, OUT> setDefaultValue(Supplier<OUT> value) {
      this.defaultValue = value;
      return this;
   }

   @Nullable
   public OUT interpolate(ModelBone bone, IAnimationProperty property) {
      if (this.isEmpty()) {
         return this.defaultValue.get();
      }

      float time = (float)property.getTime();
      Entry<Float, IN> lastEntry = this.floorEntry(time);
      if (lastEntry != null && Float.compare(lastEntry.getKey(), time) == 0) {
         return lastEntry.getValue().getValue(0, property);
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
      if (nextKey == lastKey) {
         return lastEntry.getValue().getValue(0, property);
      }

      float t = (time - lastKey) / (nextKey - lastKey);
      IN nextFrame = nextEntry.getValue();
      IN prevFrame = lastEntry.getValue();
      OUT next = nextFrame.getValue(0, property);
      OUT prev = prevFrame.getValue(0, property);
      return this.interpolateFunc.interpolate(new KeyframeInterpolator.Context<>(lastKey, nextKey, prevFrame, nextFrame, property, bone, this), prev, next, t);
   }

   public OUT interpolateAndDerive(ModelBone bone, IAnimationProperty property) {
      throw new UnsupportedOperationException();
   }

   public float getHigherKey(float time) {
      Float high = this.higherKey(time);
      return high == null ? this.lastKey() : high;
   }

   public float getLowerKey(float time) {
      Float low = this.lowerKey(time);
      return low == null ? this.firstKey() : low;
   }

   @AllArgsConstructor
   public static class Context<IN extends AbstractKeyframe<OUT>, OUT> {
      public final float prevKey;
      public final float nextKey;
      public final IN prevFrame;
      public final IN nextFrame;
      public final IAnimationProperty property;
      public final ModelBone bone;
      public final KeyframeInterpolator<IN, OUT> interpolator;
   }

   @FunctionalInterface
   public interface Interpolation<IN extends AbstractKeyframe<OUT>, OUT> {
      OUT interpolate(KeyframeInterpolator.Context<IN, OUT> context, OUT out, OUT out2, float value);
   }
}
