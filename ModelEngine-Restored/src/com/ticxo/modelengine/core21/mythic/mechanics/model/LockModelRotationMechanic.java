package com.ticxo.modelengine.core21.mythic.mechanics.model;

import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Optional;

@MythicMechanic(name = "lockmodel", aliases = "lockrotation")
public class LockModelRotationMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderBoolean lock;

   public LockModelRotationMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.lock = mlc.getPlaceholderBoolean(new String[]{"l", "lock"}, true);
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, target.getUniqueId());
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      boolean lock = this.lock.get(meta, target);
      if (modelId != null) {
         MythicUtils.executeOptModelId(model, modelId, activeModel -> activeModel.setModelRotationLocked(lock));
      } else {
         model.setModelRotationLocked(lock);
      }

      return SkillResult.SUCCESS;
   }

   public SkillResult cast(SkillMetadata meta) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, null);
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta);
      boolean lock = this.lock.get(meta);
      if (modelId != null) {
         MythicUtils.executeOptModelId(model, modelId, activeModel -> activeModel.setModelRotationLocked(lock));
      } else {
         model.setModelRotationLocked(lock);
      }

      return SkillResult.SUCCESS;
   }
}
