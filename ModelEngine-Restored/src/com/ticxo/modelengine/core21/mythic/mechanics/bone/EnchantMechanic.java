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
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Optional;
import java.util.Map.Entry;

@MythicMechanic(name = "enchant", aliases = {})
public class EnchantMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderString partId;
   private final boolean enchant;
   private final boolean exactMatch;
   private final boolean child;

   public EnchantMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
      this.enchant = mlc.getBoolean(new String[]{"en", "enchant"}, true);
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
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      String partId = MythicUtils.getOrNullLowercase(this.partId, meta, target);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> this.enchant(activeModel, partId));
      return SkillResult.SUCCESS;
   }

   public SkillResult cast(SkillMetadata meta) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, null);
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta);
      String partId = MythicUtils.getOrNullLowercase(this.partId, meta);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> this.enchant(activeModel, partId));
      return SkillResult.SUCCESS;
   }

   private void enchant(ActiveModel activeModel, String partId) {
      if (!partId.isBlank()) {
         if (this.exactMatch) {
            activeModel.getBone(partId).ifPresent(bone -> MythicUtils.executeBoneChild(this.child, bone, v -> v.setEnchanted(this.enchant)));
         } else {
            for (Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
               if (entry.getKey().contains(partId)) {
                  MythicUtils.executeBoneChild(this.child, entry.getValue(), bone -> bone.setEnchanted(this.enchant));
               }
            }
         }
      } else {
         for (ModelBone value : activeModel.getBones().values()) {
            value.setEnchanted(this.enchant);
         }
      }
   }
}
