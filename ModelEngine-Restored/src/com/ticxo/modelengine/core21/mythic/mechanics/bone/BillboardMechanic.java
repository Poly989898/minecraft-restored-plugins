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
import java.util.Locale;
import java.util.Optional;
import java.util.Map.Entry;
import org.bukkit.entity.Display.Billboard;

@MythicMechanic(name = "billboard", aliases = {})
public class BillboardMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderString partId;
   private final PlaceholderString billboard;
   private final boolean exactMatch;
   private final boolean child;

   public BillboardMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
      this.billboard = mlc.getPlaceholderString(new String[]{"b", "bb", "bill", "billboard"}, null, new String[0]);
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

      try {
         String billboard = MythicUtils.getOrNull(this.billboard, meta, target).toUpperCase(Locale.ENGLISH);
         Billboard bb = Billboard.valueOf(billboard);
         String partId = MythicUtils.getOrNullLowercase(this.partId, meta, target);
         String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
         MythicUtils.executeOptModelId(model, modelId, activeModel -> this.billboard(activeModel, partId, bb));
         return SkillResult.SUCCESS;
      } catch (Exception e) {
         e.printStackTrace();
         return SkillResult.ERROR;
      }
   }

   public SkillResult cast(SkillMetadata meta) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, null);
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();

      try {
         String billboard = MythicUtils.getOrNull(this.billboard, meta).toUpperCase(Locale.ENGLISH);
         Billboard bb = Billboard.valueOf(billboard);
         String partId = MythicUtils.getOrNullLowercase(this.partId, meta);
         String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta);
         MythicUtils.executeOptModelId(model, modelId, activeModel -> this.billboard(activeModel, partId, bb));
         return SkillResult.SUCCESS;
      } catch (Exception e) {
         e.printStackTrace();
         return SkillResult.ERROR;
      }
   }

   private void billboard(ActiveModel activeModel, String partId, Billboard billboard) {
      if (partId.isBlank()) {
         activeModel.setBillboard(billboard);
      } else if (this.exactMatch) {
         activeModel.getBone(partId).ifPresent(bone -> MythicUtils.executeBoneChild(this.child, bone, v -> v.setBillboard(billboard)));
      } else {
         for (Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
            if (entry.getKey().contains(partId)) {
               MythicUtils.executeBoneChild(this.child, entry.getValue(), bone -> bone.setBillboard(billboard));
            }
         }
      }
   }
}
