package com.ticxo.modelengine.api.model.bone.manager;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractBehaviorManager<T extends BoneBehavior> implements BehaviorManager<T> {
   protected final ActiveModel activeModel;
   protected final BoneBehaviorType<T> type;

   protected Optional<T> getBoneBehavior(String boneId) {
      Optional<ModelBone> bone = this.getActiveModel().getBone(boneId);
      return bone.isEmpty() ? Optional.empty() : bone.get().getBoneBehavior(this.getType());
   }
}
