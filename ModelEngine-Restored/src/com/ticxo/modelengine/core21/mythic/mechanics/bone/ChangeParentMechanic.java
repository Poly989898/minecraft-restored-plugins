package com.ticxo.modelengine.core21.mythic.mechanics.bone;

import com.ticxo.modelengine.api.model.ActiveModel;
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
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Optional;

@MythicMechanic(name = "changeparent", aliases = {})
public class ChangeParentMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderString parentPart;
   private final PlaceholderString childPart;

   public ChangeParentMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.parentPart = mlc.getPlaceholderString(new String[]{"p", "parent"}, null, new String[0]);
      this.childPart = mlc.getPlaceholderString(new String[]{"c", "child"}, null, new String[0]);
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
      ActiveModel activeModel = MythicUtils.getActiveModelOrNull(model, modelId);
      if (activeModel == null) {
         return SkillResult.CONDITION_FAILED;
      }

      String parentPart = MythicUtils.getOrNullLowercase(this.parentPart, meta, target);
      String childPart = MythicUtils.getOrNullLowercase(this.childPart, meta, target);
      activeModel.getBone(parentPart)
         .ifPresent(parent -> activeModel.getBone(childPart).ifPresent(child -> model.registerTickTask(ModeledEntity.Phase.PRE_MODEL_TICK, entity -> {
            child.setParent(parent);
            return true;
         })));
      return SkillResult.SUCCESS;
   }

   public SkillResult cast(SkillMetadata meta) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, null);
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta);
      ActiveModel activeModel = MythicUtils.getActiveModelOrNull(model, modelId);
      if (activeModel == null) {
         return SkillResult.CONDITION_FAILED;
      }

      String parentPart = MythicUtils.getOrNullLowercase(this.parentPart, meta);
      String childPart = MythicUtils.getOrNullLowercase(this.childPart, meta);
      activeModel.getBone(parentPart)
         .ifPresent(parent -> activeModel.getBone(childPart).ifPresent(child -> model.registerTickTask(ModeledEntity.Phase.PRE_MODEL_TICK, entity -> {
            child.setParent(parent);
            return true;
         })));
      return SkillResult.SUCCESS;
   }
}
