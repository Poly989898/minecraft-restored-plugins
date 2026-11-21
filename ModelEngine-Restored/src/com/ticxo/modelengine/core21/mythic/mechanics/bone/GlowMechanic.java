package com.ticxo.modelengine.core21.mythic.mechanics.bone;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillAudience;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

@MythicMechanic(name = "glow", aliases = "glowbone")
public class GlowMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderString partId;
   private final PlaceholderString color;
   private final boolean glowing;
   private final boolean exactMatch;
   private final boolean child;
   private final SkillAudience audience;

   public GlowMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
      this.glowing = mlc.getBoolean(new String[]{"g", "glow"}, true);
      this.color = mlc.getPlaceholderString(new String[]{"c", "color"}, null, new String[0]);
      this.exactMatch = mlc.getBoolean(new String[]{"em", "exact", "match", "exactmatch"}, true);
      this.child = mlc.getBoolean(new String[]{"c", "child"}, false);
      this.audience = mlc.getAudience("audience", null);
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
      String colorString = MythicUtils.getOrNull(this.color, meta, target);
      Integer color;
      if (colorString != null) {
         if (colorString.startsWith("#")) {
            colorString = colorString.substring(1);
         }

         color = Integer.parseInt(colorString, 16);
      } else {
         color = null;
      }

      String partId = MythicUtils.getOrNullLowercase(this.partId, meta, target);
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      Set<UUID> audienceSet = this.resolveAudience(meta, target);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> this.glow(activeModel, partId, color, audienceSet));
      return SkillResult.SUCCESS;
   }

   public SkillResult cast(SkillMetadata meta) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, null);
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      String colorString = MythicUtils.getOrNull(this.color, meta);
      Integer color;
      if (colorString != null) {
         if (colorString.startsWith("#")) {
            colorString = colorString.substring(1);
         }

         color = Integer.parseInt(colorString, 16);
      } else {
         color = null;
      }

      String partId = MythicUtils.getOrNullLowercase(this.partId, meta);
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta);
      Set<UUID> audienceSet = this.resolveAudience(meta, null);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> this.glow(activeModel, partId, color, audienceSet));
      return SkillResult.SUCCESS;
   }

   @Nullable
   private Set<UUID> resolveAudience(SkillMetadata meta, @Nullable AbstractEntity target) {
      if (this.audience == null) {
         return null;
      }

      Set<UUID> uuids = new HashSet<>();

      for (AbstractPlayer player : this.audience.get(meta, target)) {
         uuids.add(player.getUniqueId());
      }

      return uuids;
   }

   private void glow(ActiveModel activeModel, String partId, Integer color, @Nullable Set<UUID> audienceSet) {
      if (partId.isBlank()) {
         activeModel.setGlowing(this.glowing);
         activeModel.setGlowColor(color);
         activeModel.setGlowAudience(audienceSet);
      } else if (this.exactMatch) {
         activeModel.getBone(partId).ifPresent(bone -> MythicUtils.executeBoneChild(this.child, bone, bone1 -> this.setGlow(bone1, color, audienceSet)));
      } else {
         for (Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
            if (entry.getKey().contains(partId)) {
               MythicUtils.executeBoneChild(this.child, entry.getValue(), bone -> this.setGlow(bone, color, audienceSet));
            }
         }
      }
   }

   private void setGlow(ModelBone bone, Integer color, @Nullable Set<UUID> audienceSet) {
      bone.setGlowing(this.glowing);
      bone.setGlowColor(color);
      bone.setGlowAudience(audienceSet);
   }
}
