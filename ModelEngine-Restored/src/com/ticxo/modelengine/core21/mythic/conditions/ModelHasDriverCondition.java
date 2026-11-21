package com.ticxo.modelengine.core21.mythic.conditions;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.core21.mythic.utils.MythicCondition;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import java.util.Optional;

@MythicCondition(name = "modelhasdriver", aliases = "modeldriver")
public class ModelHasDriverCondition implements IEntityCondition {
   public boolean check(AbstractEntity abstractEntity) {
      ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(abstractEntity.getUniqueId());
      if (modeledEntity == null) {
         return false;
      }

      for (ActiveModel activeModel : modeledEntity.getModels().values()) {
         Optional maybeMountManager = activeModel.getMountManager();
         if (!maybeMountManager.isEmpty() && ((MountManager)((BehaviorManager)maybeMountManager.get())).isControlled()) {
            return true;
         }
      }

      return false;
   }
}
