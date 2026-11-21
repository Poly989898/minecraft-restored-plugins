package com.ticxo.modelengine.api.animation.keyframe.data;

import com.ticxo.modelengine.api.animation.property.IAnimationProperty;

public interface IKeyframeData {
   IKeyframeData EMPTY = property -> 0.0;

   double getValue(IAnimationProperty property);
}
