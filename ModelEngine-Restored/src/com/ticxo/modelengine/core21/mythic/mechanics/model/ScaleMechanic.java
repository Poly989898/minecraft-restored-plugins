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
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Optional;

@MythicMechanic(name = "scale", aliases = {})
public class ScaleMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderDouble scale;
   private final boolean isHitbox;

   public ScaleMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.scale = mlc.getPlaceholderDouble(new String[]{"s", "scale"}, "1", new String[0]);
      this.isHitbox = mlc.getBoolean(new String[]{"h", "hitbox"}, false);
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
      double scale = this.scale.get(meta, target);
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> {
         if (this.isHitbox) {
            activeModel.setHitboxScale(scale);
         } else {
            activeModel.setScale(scale);
         }
      });
      return SkillResult.SUCCESS;
   }

   public SkillResult cast(SkillMetadata meta) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, null);
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      double scale = this.scale.get(meta);
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> {
         if (this.isHitbox) {
            activeModel.setHitboxScale(scale);
         } else {
            activeModel.setScale(scale);
         }
      });
      return SkillResult.SUCCESS;
   }
}
