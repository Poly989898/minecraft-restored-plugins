package com.ticxo.modelengine.core21.mythic.mechanics.model;

import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
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

@MythicMechanic(name = "submodel", aliases = {})
public class SubModelMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderString parentId;
   private final PlaceholderString childId;
   private final PlaceholderString prefix;
   private final boolean remove;

   public SubModelMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.parentId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, null, new String[0]);
      this.childId = mlc.getPlaceholderString(new String[]{"sp", "spid", "subpart", "subpartid"}, null, new String[0]);
      this.prefix = mlc.getPlaceholderString(new String[]{"pfx", "prefix"}, "", new String[0]);
      this.remove = mlc.getBoolean(new String[]{"r", "remove"}, false);
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

      String childId = MythicUtils.getOrNullLowercase(this.childId, meta, target);
      if (childId == null) {
         return SkillResult.INVALID_CONFIG;
      }

      if (this.remove) {
         activeModel.getBone(childId).ifPresent(ModelBone::destroy);
      } else {
         String parentId = MythicUtils.getOrNullLowercase(this.parentId, meta, target);
         String prefix = MythicUtils.getOrNullLowercase(this.prefix, meta, target);
         BlueprintBone childBone = activeModel.getBlueprint().getFlatMap().get(childId);
         if (childBone == null) {
            return SkillResult.INVALID_CONFIG;
         }

         activeModel.forceGenerateBone(parentId, prefix, childBone);
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
      ActiveModel activeModel = MythicUtils.getActiveModelOrNull(model, modelId);
      if (activeModel == null) {
         return SkillResult.CONDITION_FAILED;
      }

      String childId = MythicUtils.getOrNullLowercase(this.childId, meta);
      if (childId == null) {
         return SkillResult.INVALID_CONFIG;
      }

      if (this.remove) {
         activeModel.getBone(childId).ifPresent(ModelBone::destroy);
      } else {
         String parentId = MythicUtils.getOrNullLowercase(this.parentId, meta);
         String prefix = MythicUtils.getOrNullLowercase(this.prefix, meta);
         BlueprintBone childBone = activeModel.getBlueprint().getFlatMap().get(childId);
         if (childBone == null) {
            return SkillResult.INVALID_CONFIG;
         }

         activeModel.forceGenerateBone(parentId, prefix, childBone);
      }

      return SkillResult.SUCCESS;
   }
}
