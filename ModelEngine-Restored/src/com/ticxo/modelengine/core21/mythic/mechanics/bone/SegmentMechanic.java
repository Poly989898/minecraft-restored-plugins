package com.ticxo.modelengine.core21.mythic.mechanics.bone;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.Segment;
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
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Optional;
import java.util.Map.Entry;

@MythicMechanic(name = "segment", aliases = "seg")
public class SegmentMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderString partId;
   private final PlaceholderBoolean bounded;
   private final PlaceholderFloat angleLimit;
   private final PlaceholderFloat x;
   private final PlaceholderFloat y;
   private final PlaceholderFloat z;
   private final PlaceholderFloat minX;
   private final PlaceholderFloat maxX;
   private final PlaceholderFloat minY;
   private final PlaceholderFloat maxY;
   private final PlaceholderFloat minZ;
   private final PlaceholderFloat maxZ;
   private final PlaceholderFloat extendRate;
   private final PlaceholderFloat elasticity;
   private final boolean exactMatch;

   public SegmentMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
      this.bounded = mlc.getPlaceholderBoolean(new String[]{"b", "bd", "bounded"}, null);
      this.angleLimit = mlc.getPlaceholderFloat(new String[]{"a", "al", "angle", "anglelimit"}, null, new String[0]);
      this.x = mlc.getPlaceholderFloat(new String[]{"x"}, null, new String[0]);
      this.y = mlc.getPlaceholderFloat(new String[]{"y"}, null, new String[0]);
      this.z = mlc.getPlaceholderFloat(new String[]{"z"}, null, new String[0]);
      this.minX = mlc.getPlaceholderFloat(new String[]{"minx"}, null, new String[0]);
      this.maxX = mlc.getPlaceholderFloat(new String[]{"maxx"}, null, new String[0]);
      this.minY = mlc.getPlaceholderFloat(new String[]{"miny"}, null, new String[0]);
      this.maxY = mlc.getPlaceholderFloat(new String[]{"maxy"}, null, new String[0]);
      this.minZ = mlc.getPlaceholderFloat(new String[]{"minz"}, null, new String[0]);
      this.maxZ = mlc.getPlaceholderFloat(new String[]{"maxz"}, null, new String[0]);
      this.extendRate = mlc.getPlaceholderFloat(new String[]{"e", "er", "extend", "extendrate"}, null, new String[0]);
      this.elasticity = mlc.getPlaceholderFloat(new String[]{"el", "elasticity"}, null, new String[0]);
      this.exactMatch = mlc.getBoolean(new String[]{"em", "exact", "match", "exactmatch"}, true);
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
      Boolean bounded = this.bounded == null ? null : this.bounded.get(meta, target);
      Float angleLimit = this.angleLimit == null ? null : this.angleLimit.get(meta, target);
      Float x = this.x == null ? angleLimit : this.x.get(meta, target);
      Float y = this.y == null ? angleLimit : this.y.get(meta, target);
      Float z = this.z == null ? angleLimit : this.z.get(meta, target);
      Float minX = this.minX == null ? this.negate(x) : this.minX.get(meta, target);
      Float maxX = this.maxX == null ? x : this.maxX.get(meta, target);
      Float minY = this.minY == null ? this.negate(y) : this.minY.get(meta, target);
      Float maxY = this.maxY == null ? y : this.maxY.get(meta, target);
      Float minZ = this.minZ == null ? this.negate(z) : this.minZ.get(meta, target);
      Float maxZ = this.maxZ == null ? z : this.maxZ.get(meta, target);
      Float extendRate = this.extendRate == null ? null : this.extendRate.get(meta, target);
      Float elasticity = this.elasticity == null ? null : this.elasticity.get(meta, target);
      MythicUtils.executeOptModelId(
         model, modelId, activeModel -> this.segment(activeModel, partId, bounded, minX, maxX, minY, maxY, minZ, maxZ, extendRate, elasticity)
      );
      return SkillResult.SUCCESS;
   }

   public SkillResult cast(SkillMetadata meta) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, null);
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      String partId = MythicUtils.getOrNullLowercase(this.partId, meta);
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta);
      Boolean bounded = this.bounded == null ? null : this.bounded.get(meta);
      Float angleLimit = this.angleLimit == null ? null : this.angleLimit.get(meta);
      Float x = this.x == null ? angleLimit : this.x.get(meta);
      Float y = this.y == null ? angleLimit : this.y.get(meta);
      Float z = this.z == null ? angleLimit : this.z.get(meta);
      Float minX = this.minX == null ? this.negate(x) : this.minX.get(meta);
      Float maxX = this.maxX == null ? x : this.maxX.get(meta);
      Float minY = this.minY == null ? this.negate(y) : this.minY.get(meta);
      Float maxY = this.maxY == null ? y : this.maxY.get(meta);
      Float minZ = this.minZ == null ? this.negate(z) : this.minZ.get(meta);
      Float maxZ = this.maxZ == null ? z : this.maxZ.get(meta);
      Float extendRate = this.extendRate == null ? null : this.extendRate.get(meta);
      Float elasticity = this.elasticity == null ? null : this.elasticity.get(meta);
      MythicUtils.executeOptModelId(
         model, modelId, activeModel -> this.segment(activeModel, partId, bounded, minX, maxX, minY, maxY, minZ, maxZ, extendRate, elasticity)
      );
      return SkillResult.SUCCESS;
   }

   private Float negate(Float val) {
      return val == null ? null : -val;
   }

   private void segment(
      ActiveModel activeModel,
      String partId,
      Boolean bounded,
      Float minX,
      Float maxX,
      Float minY,
      Float maxY,
      Float minZ,
      Float maxZ,
      Float extendRate,
      Float elasticity
   ) {
      if (!partId.isBlank()) {
         if (this.exactMatch) {
            activeModel.getBone(partId).ifPresent(bone -> this.setIfSegment(bone, bounded, minX, maxX, minY, maxY, minZ, maxZ, extendRate, elasticity));
         } else {
            for (Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
               if (entry.getKey().contains(partId)) {
                  this.setIfSegment(entry.getValue(), bounded, minX, maxX, minY, maxY, minZ, maxZ, extendRate, elasticity);
               }
            }
         }
      } else {
         for (Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
            this.setIfSegment(entry.getValue(), bounded, minX, maxX, minY, maxY, minZ, maxZ, extendRate, elasticity);
         }
      }
   }

   private void setIfSegment(
      ModelBone bone, Boolean bounded, Float minX, Float maxX, Float minY, Float maxY, Float minZ, Float maxZ, Float extendRate, Float elasticity
   ) {
      bone.getBoneBehavior(BoneBehaviorTypes.SEGMENT).ifPresent(segment -> {
         if (bounded != null) {
            ((Segment)segment).setBounded(bounded);
         }

         if (minX != null) {
            ((Segment)segment).setMinX(minX);
         }

         if (maxX != null) {
            ((Segment)segment).setMaxX(maxX);
         }

         if (minY != null) {
            ((Segment)segment).setMinY(minY);
         }

         if (maxY != null) {
            ((Segment)segment).setMaxY(maxY);
         }

         if (minZ != null) {
            ((Segment)segment).setMinZ(minZ);
         }

         if (maxZ != null) {
            ((Segment)segment).setMaxZ(maxZ);
         }

         if (extendRate != null) {
            ((Segment)segment).setAlignRate(extendRate);
         }

         if (elasticity != null) {
            ((Segment)segment).setElasticity(elasticity);
         }
      });
      bone.getBoneBehavior(BoneBehaviorTypes.TAIL).ifPresent(segment -> {
         if (bounded != null) {
            ((Segment)segment).setBounded(bounded);
         }

         if (minX != null) {
            ((Segment)segment).setMinX(minX);
         }

         if (maxX != null) {
            ((Segment)segment).setMaxX(maxX);
         }

         if (minY != null) {
            ((Segment)segment).setMinY(minY);
         }

         if (maxY != null) {
            ((Segment)segment).setMaxY(maxY);
         }

         if (minZ != null) {
            ((Segment)segment).setMinZ(minZ);
         }

         if (maxZ != null) {
            ((Segment)segment).setMaxZ(maxZ);
         }

         if (extendRate != null) {
            ((Segment)segment).setAlignRate(extendRate);
         }

         if (elasticity != null) {
            ((Segment)segment).setElasticity(elasticity);
         }
      });
   }
}
