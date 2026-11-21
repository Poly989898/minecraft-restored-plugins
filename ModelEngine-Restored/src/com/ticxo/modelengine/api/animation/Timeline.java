package com.ticxo.modelengine.api.animation;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.animation.interpolator.KeyframeInterpolator;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeTypes;
import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Timeline {
   @Getter
   private final BlueprintAnimation animation;
   @Getter
   private final boolean globalRotation;
   private final Map<KeyframeType<?, ?>, KeyframeInterpolator<?, ?>> interpolators = Maps.newConcurrentMap();
   private volatile KeyframeInterpolator<?, ?> position;
   private volatile KeyframeInterpolator<?, ?> rotation;
   private volatile KeyframeInterpolator<?, ?> scale;

   public boolean hasInterpolator(KeyframeType<?, ?> type) {
      return this.getCached(type) != null ? true : this.interpolators.containsKey(type);
   }

   public <KEY extends AbstractKeyframe<DATA>, DATA> KeyframeInterpolator<KEY, DATA> getInterpolator(KeyframeType<KEY, DATA> type) {
      KeyframeInterpolator<? extends AbstractKeyframe<?>, ?> cached = (KeyframeInterpolator<? extends AbstractKeyframe<?>, ?>)this.getCached(type);
      if (cached != null) {
         return (KeyframeInterpolator<KEY, DATA>)cached;
      }

      KeyframeInterpolator<? extends AbstractKeyframe<?>, ?> interpolator = (KeyframeInterpolator<? extends AbstractKeyframe<?>, ?>)this.interpolators
         .get(type);
      if (interpolator == null) {
         interpolator = (KeyframeInterpolator<? extends AbstractKeyframe<?>, ?>)this.interpolators
            .computeIfAbsent(type, keyframeType -> keyframeType.createInterpolator(this));
      }

      this.setCached(type, interpolator);
      return (KeyframeInterpolator<KEY, DATA>)interpolator;
   }

   public <KEY extends AbstractKeyframe<DATA>, DATA> KEY getKeyframe(float time, KeyframeType<KEY, DATA> type) {
      KeyframeInterpolator<KEY, DATA> interpolator = this.getInterpolator(type);
      return interpolator.computeIfAbsent(time, t -> type.createKeyframe());
   }

   private KeyframeInterpolator<?, ?> getCached(KeyframeType<?, ?> type) {
      if (type == KeyframeTypes.POSITION) {
         return this.position;
      } else if (type == KeyframeTypes.ROTATION) {
         return this.rotation;
      } else {
         return type == KeyframeTypes.SCALE ? this.scale : null;
      }
   }

   private void setCached(KeyframeType<?, ?> type, KeyframeInterpolator<?, ?> interpolator) {
      if (type == KeyframeTypes.POSITION) {
         this.position = interpolator;
      } else if (type == KeyframeTypes.ROTATION) {
         this.rotation = interpolator;
      } else if (type == KeyframeTypes.SCALE) {
         this.scale = interpolator;
      }
   }
}
