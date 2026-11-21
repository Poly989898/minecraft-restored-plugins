package com.ticxo.modelengine.api.animation;

import com.ticxo.modelengine.api.animation.interpolator.KeyframeInterpolator;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeTypes;
import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.animation.keyframe.type.ScriptKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BlueprintAnimation {
   private final ModelBlueprint modelBlueprint;
   private final Map<UUID, Timeline> timelines = new HashMap<>();
   private final String name;
   private final Timeline globalTimeline = new Timeline(this, false);
   private double length;
   private BlueprintAnimation.LoopMode loopMode;
   private boolean override;

   public Vector3f getPosition(ModelBone bone, IAnimationProperty property) {
      return this.getPosition(bone, bone.getBlueprintBone(), property);
   }

   public Vector3f getPosition(ModelBone bone, BlueprintBone blueprintBone, IAnimationProperty property) {
      Timeline timeline = this.timelines.get(blueprintBone.getUuid());
      return timeline == null ? null : timeline.getInterpolator(KeyframeTypes.POSITION).interpolate(bone, property);
   }

   public Vector3f getVelocity(ModelBone bone, IAnimationProperty property) {
      return this.getVelocity(bone, bone.getBlueprintBone(), property);
   }

   public Vector3f getVelocity(ModelBone bone, BlueprintBone blueprintBone, IAnimationProperty property) {
      Timeline timeline = this.timelines.get(blueprintBone.getUuid());
      return timeline == null ? null : timeline.getInterpolator(KeyframeTypes.POSITION).interpolateAndDerive(bone, property);
   }

   public Vector3f getRotation(ModelBone bone, IAnimationProperty property) {
      return this.getRotation(bone, bone.getBlueprintBone(), property);
   }

   public Vector3f getRotation(ModelBone bone, BlueprintBone blueprintBone, IAnimationProperty property) {
      Timeline timeline = this.timelines.get(blueprintBone.getUuid());
      return timeline == null ? null : timeline.getInterpolator(KeyframeTypes.ROTATION).interpolate(bone, property);
   }

   public Vector3f getScale(ModelBone bone, IAnimationProperty property) {
      return this.getScale(bone, bone.getBlueprintBone(), property);
   }

   public Vector3f getScale(ModelBone bone, BlueprintBone blueprintBone, IAnimationProperty property) {
      Timeline timeline = this.timelines.get(blueprintBone.getUuid());
      return timeline == null ? null : timeline.getInterpolator(KeyframeTypes.SCALE).interpolate(bone, property);
   }

   public List<ScriptKeyframe.Script> getScript(IAnimationProperty property) {
      return this.globalTimeline.getInterpolator(KeyframeTypes.SCRIPT).interpolate(null, property);
   }

   public String getInterpolation(KeyframeType<?, ?> type, UUID uuid, float time) {
      Timeline timeline = this.timelines.get(uuid);
      if (timeline == null) {
         return null;
      }

      KeyframeInterpolator<? extends AbstractKeyframe<?>, ?> interpolator = timeline.getInterpolator((KeyframeType<? extends AbstractKeyframe<?>, ?>)type);
      AbstractKeyframe<?> frame = (AbstractKeyframe<?>)interpolator.get(time);
      if (frame != null) {
         return frame.getInterpolation();
      }

      frame = (AbstractKeyframe<?>)interpolator.get(interpolator.getLowerKey(time));
      return frame == null ? null : frame.getInterpolation();
   }

   @Generated
   public ModelBlueprint getModelBlueprint() {
      return this.modelBlueprint;
   }

   @Generated
   public Map<UUID, Timeline> getTimelines() {
      return this.timelines;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public Timeline getGlobalTimeline() {
      return this.globalTimeline;
   }

   @Generated
   public double getLength() {
      return this.length;
   }

   @Generated
   public BlueprintAnimation.LoopMode getLoopMode() {
      return this.loopMode;
   }

   @Generated
   public boolean isOverride() {
      return this.override;
   }

   @Generated
   public BlueprintAnimation(ModelBlueprint modelBlueprint, String name) {
      this.modelBlueprint = modelBlueprint;
      this.name = name;
   }

   @Generated
   public void setLength(double length) {
      this.length = length;
   }

   @Generated
   public void setLoopMode(BlueprintAnimation.LoopMode loopMode) {
      this.loopMode = loopMode;
   }

   @Generated
   public void setOverride(boolean override) {
      this.override = override;
   }

   public enum LoopMode {
      ONCE,
      HOLD,
      LOOP;

      public static BlueprintAnimation.LoopMode get(String mode) {
         try {
            return valueOf(mode.toUpperCase(Locale.ENGLISH));
         } catch (IllegalArgumentException e) {
            return ONCE;
         }
      }

      @Nullable
      public static BlueprintAnimation.LoopMode getOrNull(String mode) {
         if (mode == null) {
            return null;
         }

         try {
            return valueOf(mode.toUpperCase(Locale.ENGLISH));
         } catch (IllegalArgumentException e) {
            return null;
         }
      }
   }

   public enum OverrideMode {
      NONE,
      OVERRIDE;

      public static BlueprintAnimation.OverrideMode get(String mode) {
         try {
            return valueOf(mode.toUpperCase(Locale.ENGLISH));
         } catch (IllegalArgumentException e) {
            return NONE;
         }
      }

      @Nullable
      public static BlueprintAnimation.OverrideMode getOrNull(String mode) {
         if (mode == null) {
            return null;
         }

         try {
            return valueOf(mode.toUpperCase(Locale.ENGLISH));
         } catch (IllegalArgumentException e) {
            if ("true".equalsIgnoreCase(mode)) {
               return OVERRIDE;
            } else {
               return "false".equalsIgnoreCase(mode) ? NONE : null;
            }
         }
      }
   }
}
