package com.ticxo.modelengine.api.animation.keyframe.data;

import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DoubleData implements IKeyframeData {
   private final double data;

   @Override
   public double getValue(IAnimationProperty property) {
      return this.data;
   }
}
