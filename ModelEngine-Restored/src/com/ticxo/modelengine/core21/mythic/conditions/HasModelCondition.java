package com.ticxo.modelengine.core21.mythic.conditions;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicCondition;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Optional;

@MythicCondition(name = "hasmodel")
public class HasModelCondition implements IEntityCondition {
   private final PlaceholderString modelId;
   private final PlaceholderString state;

   public HasModelCondition(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.state = mlc.getPlaceholderString(new String[]{"s", "state"}, null, new String[0]);
   }

   public boolean check(AbstractEntity abstractEntity) {
      ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(abstractEntity.getUniqueId());
      if (modeledEntity == null) {
         return false;
      }

      String modelId = MythicUtils.getOrNullLowercase(this.modelId, abstractEntity);
      String state = MythicUtils.getOrNullLowercase(this.state, abstractEntity);
      if (modelId == null) {
         if (state == null) {
            return !modeledEntity.getModels().isEmpty();
         }

         for (ActiveModel activeModel : modeledEntity.getModels().values()) {
            if (activeModel.getAnimationHandler().isPlayingAnimation(state)) {
               return true;
            }
         }

         return false;
      } else {
         Optional<ActiveModel> maybeModel = modeledEntity.getModel(modelId);
         return maybeModel.isEmpty() ? false : state == null || maybeModel.get().getAnimationHandler().isPlayingAnimation(state);
      }
   }
}
