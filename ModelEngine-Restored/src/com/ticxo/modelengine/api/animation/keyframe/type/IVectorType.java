package com.ticxo.modelengine.api.animation.keyframe.type;

import com.ticxo.modelengine.api.animation.keyframe.data.IKeyframeData;

public interface IVectorType {
   IVectorType setX(IKeyframeData x);

   IVectorType setY(IKeyframeData y);

   IVectorType setZ(IKeyframeData z);

   IVectorType setPostX(IKeyframeData x);

   IVectorType setPostY(IKeyframeData y);

   IVectorType setPostZ(IKeyframeData z);

   IVectorType setXFactor(float x);

   IVectorType setYFactor(float y);

   IVectorType setZFactor(float z);
}
