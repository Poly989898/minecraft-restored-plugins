package com.ticxo.modelengine.core21.mythic.mechanics.controller;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;

@MythicMechanic(name = "jump", aliases = {})
public class JumpMechanic implements ITargetedEntitySkill {
   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      if (meta.getMetadata("meg:move_controller").orElseGet(() -> {
         ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
         return model == null ? null : model.getBase().getMoveController();
      }) instanceof MoveController moveController) {
         moveController.jump();
      }

      return SkillResult.SUCCESS;
   }
}
