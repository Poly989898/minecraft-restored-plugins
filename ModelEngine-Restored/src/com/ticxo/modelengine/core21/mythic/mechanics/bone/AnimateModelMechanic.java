package com.ticxo.modelengine.core21.mythic.mechanics.bone;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.SimpleManualAnimator;
import com.ticxo.modelengine.api.utils.logger.TLogger;
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
import java.util.Locale;
import java.util.Optional;
import java.util.Map.Entry;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@MythicMechanic(name = "animatemodel", aliases = {})
public class AnimateModelMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private static final String[] POSITION = new String[]{"pos", "position"};
   private static final String[] ROTATION = new String[]{"rot", "rotation"};
   private static final String[] SCALE = new String[]{"scl", "scale"};
   private final PlaceholderString modelId;
   private final PlaceholderString partId;
   private final boolean exactMatch;
   private final boolean relative;
   private final boolean override;
   private final boolean copy;
   private final PlaceholderDouble[] position = new PlaceholderDouble[3];
   private final boolean hasPosition;
   private final PlaceholderDouble[] rotation = new PlaceholderDouble[3];
   private final boolean hasRotation;
   private final PlaceholderDouble[] scale = new PlaceholderDouble[3];
   private final boolean hasScale;
   private final PlaceholderString rotationModeString;
   private AnimateModelMechanic.RotationMode rotationMode;

   public AnimateModelMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
      this.exactMatch = mlc.getBoolean(new String[]{"em", "exact", "match", "exactmatch"}, true);
      this.relative = mlc.getBoolean(new String[]{"r", "rel", "relative"}, false);
      this.override = mlc.getBoolean(new String[]{"o", "ov", "override"}, false);
      this.copy = mlc.getBoolean(new String[]{"copy"}, true);
      this.hasPosition = this.readTransform(mlc, POSITION, this.position, "position", "0");
      this.hasRotation = this.readTransform(mlc, ROTATION, this.rotation, "rotation", "0");
      this.hasScale = this.readTransform(mlc, SCALE, this.scale, "scale", "1");
      this.rotationModeString = mlc.getPlaceholderString(new String[]{"rm", "rotmode"}, "LOCAL", new String[0]);
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
      this.rotationMode = AnimateModelMechanic.RotationMode.get(this.rotationModeString.get(meta, target).toUpperCase(Locale.ENGLISH));
      MythicUtils.executeOptModelId(model, modelId, activeModel -> this.transform(activeModel, partId, meta, target));
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
      this.rotationMode = AnimateModelMechanic.RotationMode.get(this.rotationModeString.get(meta).toUpperCase(Locale.ENGLISH));
      MythicUtils.executeOptModelId(model, modelId, activeModel -> this.transform(activeModel, partId, meta, null));
      return SkillResult.SUCCESS;
   }

   private boolean readTransform(MythicLineConfig mlc, String[] attrs, PlaceholderDouble[] axis, String axisName, String def) {
      String coords = mlc.getString(attrs, null, new String[0]);
      if (coords == null) {
         return false;
      }

      String[] split = coords.split(",");

      try {
         axis[0] = PlaceholderDouble.of(split[0]);
         axis[1] = PlaceholderDouble.of(split[1]);
         axis[2] = PlaceholderDouble.of(split[2]);
      } catch (Exception e) {
         TLogger.error("The '" + axisName + "' attribute must be in the format " + axisName + "=x,y,z.");
         axis[0] = PlaceholderDouble.of(def);
         axis[1] = PlaceholderDouble.of(def);
         axis[2] = PlaceholderDouble.of(def);
      }

      return true;
   }

   private void transform(ActiveModel activeModel, String partId, SkillMetadata meta, AbstractEntity target) {
      if (this.override) {
         if (this.exactMatch) {
            activeModel.getBone(partId).ifPresent(bone -> this.transform(this.getAnimator(bone), meta, target));
            return;
         }

         for (Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
            if (entry.getKey().contains(partId)) {
               this.transform(this.getAnimator(entry.getValue()), meta, target);
            }
         }
      } else {
         if (this.exactMatch) {
            activeModel.getBone(partId).ifPresent(bone -> bone.setManualAnimator(null));
            return;
         }

         for (Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
            if (entry.getKey().contains(partId)) {
               entry.getValue().setManualAnimator(null);
            }
         }
      }
   }

   private SimpleManualAnimator getAnimator(ModelBone bone) {
      if (bone.getManualAnimator() instanceof SimpleManualAnimator animator) {
         return animator;
      } else {
         SimpleManualAnimator a = this.copy ? new SimpleManualAnimator(bone) : new SimpleManualAnimator();
         bone.setManualAnimator(a);
         return a;
      }
   }

   private void transform(SimpleManualAnimator animator, SkillMetadata meta, AbstractEntity target) {
      this.transform(this.hasPosition, animator.getPosition(), this.position, meta, target);
      this.transformRotation(this.hasRotation, animator.getRotation(), this.rotation, meta, target);
      this.transform(this.hasScale, animator.getScale(), this.scale, meta, target);
   }

   private void transform(boolean hasValue, Vector3f data, PlaceholderDouble[] values, SkillMetadata meta, AbstractEntity target) {
      if (hasValue) {
         if (this.relative) {
            data.add((float)values[0].get(meta, target), (float)values[1].get(meta, target), (float)values[2].get(meta, target));
         } else {
            data.set((float)values[0].get(meta, target), (float)values[1].get(meta, target), (float)values[2].get(meta, target));
         }
      }
   }

   private void transformRotation(boolean hasValue, Quaternionf data, PlaceholderDouble[] values, SkillMetadata meta, AbstractEntity target) {
      if (hasValue) {
         Quaternionf delta = new Quaternionf()
            .rotationZYX(
               (float)values[2].get(meta, target) * (float) (Math.PI / 180.0),
               (float)values[1].get(meta, target) * (float) (Math.PI / 180.0),
               (float)values[0].get(meta, target) * (float) (Math.PI / 180.0)
            );
         if (this.relative) {
            switch (this.rotationMode) {
               case LOCAL:
                  data.mul(delta);
                  break;
               case GLOBAL:
                  delta.mul(data, data);
            }
         } else {
            data.set(delta);
         }
      }
   }

   enum RotationMode {
      LOCAL,
      GLOBAL;

      static AnimateModelMechanic.RotationMode get(String value) {
         try {
            return valueOf(value);
         } catch (Exception ignored) {
            return LOCAL;
         }
      }
   }
}
