package com.ticxo.modelengine.core21.mythic.mechanics.model;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IPriorityHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.utils.math.Easing;
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
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Optional;

@MythicMechanic(name = "state", aliases = "animation")
public class StateMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderString srcId;
   private final PlaceholderString state;
   private final PlaceholderString forceLoopMode;
   private final PlaceholderString forceOverride;
   private final PlaceholderString lerpCurveIn;
   private final PlaceholderString lerpCurveOut;
   private final boolean remove;
   private final boolean ignoreLerp;
   private final boolean force;
   private final PlaceholderInt lerpIn;
   private final PlaceholderInt lerpOut;
   private final PlaceholderInt priority;
   private final PlaceholderDouble speed;
   private final PlaceholderBoolean skipLastFrame;
   private final PlaceholderBoolean merge;
   private final PlaceholderBoolean emptyZero;

   public StateMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.srcId = mlc.getPlaceholderString(new String[]{"src", "sid", "srcmodel", "srcmodelid"}, null, new String[0]);
      this.state = mlc.getPlaceholderString(new String[]{"s", "state"}, null, new String[0]);
      this.remove = mlc.getBoolean(new String[]{"r", "remove"}, false);
      this.speed = mlc.getPlaceholderDouble(new String[]{"sp", "speed"}, "1", new String[0]);
      this.lerpIn = mlc.getPlaceholderInteger(new String[]{"li", "lerpin"}, 0, new String[0]);
      this.lerpOut = mlc.getPlaceholderInteger(new String[]{"lo", "lerpout"}, 0, new String[0]);
      this.lerpCurveIn = mlc.getPlaceholderString(new String[]{"lci", "lerpcurvein"}, null, new String[0]);
      this.lerpCurveOut = mlc.getPlaceholderString(new String[]{"lco", "lerpcurveout"}, null, new String[0]);
      this.ignoreLerp = mlc.getBoolean(new String[]{"i", "ignorelerp"}, false);
      this.force = mlc.getBoolean(new String[]{"f", "force"}, true);
      this.priority = mlc.getPlaceholderInteger(new String[]{"p", "pr", "priority"}, 1, new String[0]);
      this.forceLoopMode = mlc.getPlaceholderString(new String[]{"l", "loop"}, null, new String[0]);
      this.forceOverride = mlc.getPlaceholderString(new String[]{"ov", "override"}, null, new String[0]);
      this.skipLastFrame = mlc.getPlaceholderBoolean(new String[]{"skip", "skiplastframe"}, null);
      this.merge = mlc.getPlaceholderBoolean(new String[]{"mg", "merge"}, false);
      this.emptyZero = mlc.getPlaceholderBoolean(new String[]{"ez", "zero"}, null);
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      return this.remove ? this.removeAnimation(meta, target) : this.addAnimation(meta, target);
   }

   public SkillResult cast(SkillMetadata meta) {
      return this.remove ? this.removeAnimation(meta, null) : this.addAnimation(meta, null);
   }

   private SkillResult removeAnimation(SkillMetadata meta, AbstractEntity target) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, target == null ? null : target.getUniqueId());
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      String state = MythicUtils.getOrNullLowercase(this.state, meta, target);
      if (state == null) {
         return SkillResult.INVALID_CONFIG;
      }

      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      String srcId = Optional.ofNullable(MythicUtils.getOrNullLowercase(this.srcId, meta, target)).orElse(modelId);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> this.removeAnimation(activeModel, srcId, state, meta, target));
      return SkillResult.SUCCESS;
   }

   private void removeAnimation(ActiveModel activeModel, String srcId, String state, SkillMetadata meta, AbstractEntity target) {
      ModelBlueprint srcBlueprint = Optional.ofNullable(MythicUtils.getBlueprintOrNull(srcId)).orElse(activeModel.getBlueprint());
      AnimationHandler handler = activeModel.getAnimationHandler();
      if (handler instanceof IStateMachineHandler stateMachineHandler) {
         int priority = this.priority.get(meta, target);
         if (this.ignoreLerp) {
            stateMachineHandler.forceStopAnimation(priority, srcBlueprint, state);
         } else {
            stateMachineHandler.stopAnimation(priority, srcBlueprint, state);
         }
      } else if (handler instanceof IPriorityHandler priorityHandler) {
         if (this.ignoreLerp) {
            priorityHandler.forceStopAnimation(srcBlueprint, state);
         } else {
            priorityHandler.stopAnimation(srcBlueprint, state);
         }
      } else if (this.ignoreLerp) {
         handler.forceStopAnimation(state);
      } else {
         handler.stopAnimation(state);
      }
   }

   private SkillResult addAnimation(SkillMetadata meta, AbstractEntity target) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, target == null ? null : target.getUniqueId());
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      String state = MythicUtils.getOrNullLowercase(this.state, meta, target);
      if (state == null) {
         return SkillResult.INVALID_CONFIG;
      }

      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      String srcId = Optional.ofNullable(MythicUtils.getOrNullLowercase(this.srcId, meta, target)).orElse(modelId);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> this.addAnimation(activeModel, srcId, state, meta, target));
      return SkillResult.SUCCESS;
   }

   private void addAnimation(ActiveModel activeModel, String srcId, String state, SkillMetadata meta, AbstractEntity target) {
      ModelBlueprint srcBlueprint = Optional.ofNullable(MythicUtils.getBlueprintOrNull(srcId)).orElse(activeModel.getBlueprint());
      int lerpIn = this.lerpIn.get(meta, target);
      int lerpOut = this.lerpOut.get(meta, target);
      double speed = this.speed.get(meta, target);
      BlueprintAnimation.LoopMode loopMode = BlueprintAnimation.LoopMode.getOrNull(MythicUtils.getOrNull(this.forceLoopMode, meta, target));
      BlueprintAnimation.OverrideMode override = BlueprintAnimation.OverrideMode.getOrNull(MythicUtils.getOrNull(this.forceOverride, meta, target));
      Boolean skip = this.skipLastFrame != null ? this.skipLastFrame.get(meta, target) : null;
      boolean merge = this.merge != null && this.merge.get(meta, target);
      Boolean emptyZero = this.emptyZero != null ? this.emptyZero.get(meta, target) : null;
      Easing curveIn = Easing.getOrNull(MythicUtils.getOrNull(this.lerpCurveIn, meta, target));
      Easing curveOut = Easing.getOrNull(MythicUtils.getOrNull(this.lerpCurveOut, meta, target));
      AnimationHandler handler = activeModel.getAnimationHandler();
      if (handler instanceof IStateMachineHandler stateMachineHandler) {
         int priority = this.priority.get(meta, target);
         IAnimationProperty property = stateMachineHandler.playAnimation(priority, srcBlueprint, state, lerpIn * 0.05, lerpOut * 0.05, speed, this.force);
         this.configProperty(loopMode, override, skip, merge, emptyZero, curveIn, curveOut, property);
      } else if (handler instanceof IPriorityHandler priorityHandler) {
         IAnimationProperty property = priorityHandler.playAnimation(srcBlueprint, state, lerpIn * 0.05, lerpOut * 0.05, speed, this.force);
         this.configProperty(loopMode, override, skip, merge, emptyZero, curveIn, curveOut, property);
      } else {
         IAnimationProperty property = handler.playAnimation(state, lerpIn * 0.05, lerpOut * 0.05, speed, this.force);
         this.configProperty(loopMode, override, skip, merge, emptyZero, curveIn, curveOut, property);
      }
   }

   private void configProperty(
      BlueprintAnimation.LoopMode loopMode,
      BlueprintAnimation.OverrideMode override,
      Boolean skip,
      boolean merge,
      Boolean emptyZero,
      Easing curveIn,
      Easing curveOut,
      IAnimationProperty property
   ) {
      if (property != null) {
         property.setMergeLerp(merge);
         if (emptyZero != null) {
            property.setEmptyZero(emptyZero);
         }

         property.setForceLoopMode(loopMode);
         if (this.forceOverride != null) {
            property.setForceOverride(override);
         }

         if (skip != null) {
            property.setSkipLastFrame(skip);
         }

         if (curveIn != null) {
            property.setLerpCurveIn(curveIn);
         }

         if (curveOut != null) {
            property.setLerpCurveOut(curveOut);
         }
      }
   }
}
