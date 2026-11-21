package com.ticxo.modelengine.core21.mythic.conditions;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.core21.mythic.utils.MythicCondition;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;

@MythicCondition(name = "drivingmodel")
public class IsDrivingModelCondition implements IEntityCondition {
   public boolean check(AbstractEntity abstractEntity) {
      ActiveModel model = ModelEngineAPI.getMountPairManager().getMountedPair(abstractEntity.getUniqueId());
      if (model == null) {
         return false;
      }

      ModeledEntity modeledEntity = model.getModeledEntity();
      if (modeledEntity == null) {
         return false;
      }

      BehaviorManager mountManager = ((MountData)modeledEntity.getMountData()).getMainMountManager();
      return mountManager == null ? false : ((MountManager)mountManager).getDriver() == abstractEntity.getBukkitEntity();
   }
}
