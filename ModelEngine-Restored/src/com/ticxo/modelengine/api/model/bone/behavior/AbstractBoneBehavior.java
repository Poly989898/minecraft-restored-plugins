package com.ticxo.modelengine.api.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractBoneBehavior<T extends BoneBehavior> implements BoneBehavior {
   protected final ModelBone bone;
   protected final BoneBehaviorType<T> type;
   protected final BoneBehaviorData data;
}
