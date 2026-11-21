package com.ticxo.modelengine.core21.mythic.mechanics.bone;

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
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Optional;
import java.util.Map.Entry;

@MythicMechanic(name = "brightness", aliases = "light")
public class BrightnessMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderString partId;
   private final PlaceholderInt blockLight;
   private final PlaceholderInt skyLight;
   private final boolean exactMatch;
   private final boolean child;

   public BrightnessMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
      this.blockLight = mlc.getPlaceholderInteger(new String[]{"block", "b"}, -1, new String[0]);
      this.skyLight = mlc.getPlaceholderInteger(new String[]{"sky", "s"}, -1, new String[0]);
      this.exactMatch = mlc.getBoolean(new String[]{"em", "exact", "match", "exactmatch"}, true);
      this.child = mlc.getBoolean(new String[]{"c", "child"}, false);
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
      String partId = MythicUtils.getOrNullLowercase(this.partId, meta, target);
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      int block = this.blockLight.get(meta, target);
      int sky = this.skyLight.get(meta, target);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> this.light(activeModel, partId, block, sky));
      return SkillResult.SUCCESS;
   }

   public SkillResult cast(SkillMetadata meta) {
      Optional<Object> maybeBullet = meta.getMetadata("bullet");
      if (maybeBullet.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      Optional<ModeledEntity> maybeModel = MythicUtils.getParentVFXModel(meta);
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      String partId = MythicUtils.getOrNullLowercase(this.partId, meta);
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta);
      int block = this.blockLight.get(meta);
      int sky = this.skyLight.get(meta);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> this.light(activeModel, partId, block, sky));
      return SkillResult.SUCCESS;
   }

   private void light(ActiveModel activeModel, String partId, int block, int sky) {
      if (partId.isBlank()) {
         activeModel.setBlockLight(block);
         activeModel.setSkyLight(sky);
      } else if (this.exactMatch) {
         activeModel.getBone(partId).ifPresent(bone -> MythicUtils.executeBoneChild(this.child, bone, v -> this.setLight(v, block, sky)));
      } else {
         for (Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
            if (entry.getKey().contains(partId)) {
               MythicUtils.executeBoneChild(this.child, entry.getValue(), bone -> this.setLight(bone, block, sky));
            }
         }
      }
   }

   private void setLight(ModelBone bone, int block, int sky) {
      bone.setBlockLight(block);
      bone.setSkyLight(sky);
   }
}
