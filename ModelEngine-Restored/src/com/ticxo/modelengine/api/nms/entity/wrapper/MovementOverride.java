package com.ticxo.modelengine.api.nms.entity.wrapper;

import com.ticxo.modelengine.api.model.ModeledEntity;

public interface MovementOverride {
   void updateMovement(MoveController moveController, ModeledEntity modeledEntity);

   void updateDirection(LookController lookController, ModeledEntity modeledEntity);
}
