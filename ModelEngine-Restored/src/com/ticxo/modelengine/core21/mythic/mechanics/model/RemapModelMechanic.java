package com.ticxo.modelengine.core21.mythic.mechanics.model;

import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
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

@MythicMechanic(name = "remapmodel", aliases = "remap")
public class RemapModelMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderString newModelId;
   private final PlaceholderString map;

   public RemapModelMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.newModelId = mlc.getPlaceholderString(new String[]{"n", "nid", "newmodel", "newmodelid"}, null, new String[0]);
      this.map = mlc.getPlaceholderString(new String[]{"map"}, null, new String[0]);
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
         return SkillResult.INVALID_CONFIG;
      }

      String newModelId = MythicUtils.getOrNullLowercase(this.newModelId, meta, target);
      ModelBlueprint blueprint = MythicUtils.getBlueprintOrNull(newModelId);
      if (blueprint == null) {
         return SkillResult.INVALID_CONFIG;
      }

      if (this.map != null) {
         String mapId = MythicUtils.getOrNullLowercase(this.map, meta, target);
         ModelBlueprint mapBlueprint = MythicUtils.getBlueprintOrNull(mapId);
         if (mapBlueprint == null) {
            return SkillResult.INVALID_CONFIG;
         }

         for (String bone : mapBlueprint.getFlatMap().keySet()) {
            BlueprintBone blueprintBone = blueprint.getFlatMap().get(bone);
            if (blueprintBone != null && blueprintBone.isRenderer()) {
               activeModel.getBone(bone).ifPresent(replaced -> {
                  if (replaced.isRenderer()) {
                     replaced.setModel(blueprintBone);
                  }
               });
            }
         }
      } else {
         for (String bone : blueprint.getFlatMap().size() < activeModel.getBones().size() ? blueprint.getFlatMap().keySet() : activeModel.getBones().keySet()) {
            BlueprintBone blueprintBone = blueprint.getFlatMap().get(bone);
            if (blueprintBone != null && blueprintBone.isRenderer()) {
               activeModel.getBone(bone).ifPresent(replaced -> {
                  if (replaced.isRenderer()) {
                     replaced.setModelScale(blueprintBone.getScale());
                     replaced.setModel(blueprintBone);
                  }
               });
            }
         }
      }

      return null;
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
         return SkillResult.INVALID_CONFIG;
      }

      String newModelId = MythicUtils.getOrNullLowercase(this.newModelId, meta);
      ModelBlueprint blueprint = MythicUtils.getBlueprintOrNull(newModelId);
      if (blueprint == null) {
         return SkillResult.INVALID_CONFIG;
      }

      if (this.map != null) {
         String mapId = MythicUtils.getOrNullLowercase(this.map, meta);
         ModelBlueprint mapBlueprint = MythicUtils.getBlueprintOrNull(mapId);
         if (mapBlueprint == null) {
            return SkillResult.INVALID_CONFIG;
         }

         for (String bone : mapBlueprint.getFlatMap().keySet()) {
            BlueprintBone blueprintBone = blueprint.getFlatMap().get(bone);
            if (blueprintBone != null && blueprintBone.isRenderer()) {
               activeModel.getBone(bone).ifPresent(replaced -> {
                  if (replaced.isRenderer()) {
                     replaced.setModel(blueprintBone);
                  }
               });
            }
         }
      } else {
         for (String bone : blueprint.getFlatMap().size() < activeModel.getBones().size() ? blueprint.getFlatMap().keySet() : activeModel.getBones().keySet()) {
            BlueprintBone blueprintBone = blueprint.getFlatMap().get(bone);
            if (blueprintBone != null && blueprintBone.isRenderer()) {
               activeModel.getBone(bone).ifPresent(replaced -> {
                  if (replaced.isRenderer()) {
                     replaced.setModelScale(blueprintBone.getScale());
                     replaced.setModel(blueprintBone);
                  }
               });
            }
         }
      }

      return null;
   }
}
