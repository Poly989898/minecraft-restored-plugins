package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;

public class TailImpl extends AbstractSegmentImpl<TailImpl> {
   public TailImpl(ModelBone bone, BoneBehaviorType<TailImpl> type, BoneBehaviorData data) {
      super(bone, type, data);
   }
}
