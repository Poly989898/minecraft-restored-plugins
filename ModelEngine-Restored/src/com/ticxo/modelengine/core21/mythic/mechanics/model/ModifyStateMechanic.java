package com.ticxo.modelengine.core21.mythic.mechanics.model;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
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
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Optional;

@MythicMechanic(name = "modifystate", aliases = {"modifyanimation", "modstate"})
public class ModifyStateMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final MythicLineConfig mlc;
   private final PlaceholderString modelId;
   private final PlaceholderString state;
   private final PlaceholderInt priority;

   public ModifyStateMechanic(MythicLineConfig mlc) {
      this.mlc = mlc;
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.state = mlc.getPlaceholderString(new String[]{"s", "state"}, null, new String[0]);
      this.priority = mlc.getPlaceholderInteger(new String[]{"p", "pr", "priority"}, 1, new String[0]);
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
      String state = MythicUtils.getOrNullLowercase(this.state, meta, target);
      model.getModel(modelId).ifPresentOrElse(activeModel -> this.configureAnimation(state, activeModel, meta, target), () -> {
         for (ActiveModel activeModel : model.getModels().values()) {
            this.configureAnimation(state, activeModel, meta, target);
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
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta);
      String state = MythicUtils.getOrNullLowercase(this.state, meta);
      model.getModel(modelId).ifPresentOrElse(activeModel -> this.configureAnimation(state, activeModel, meta, null), () -> {
         for (ActiveModel activeModel : model.getModels().values()) {
            this.configureAnimation(state, activeModel, meta, null);
         }
      });
      return SkillResult.SUCCESS;
   }

   private void configureAnimation(String state, ActiveModel activeModel, SkillMetadata meta, AbstractEntity target) {
      AnimationHandler handler = activeModel.getAnimationHandler();
      IAnimationProperty property;
      if (handler instanceof IStateMachineHandler stateMachineHandler) {
         int priority = this.priority.get(meta, target);
         property = stateMachineHandler.getAnimation(priority, state);
      } else {
         property = handler.getAnimation(state);
      }

      if (property != null) {
         PlaceholderDouble speedPlaceholder = this.mlc.getPlaceholderDouble(new String[]{"sp", "speed"}, property.getSpeed(), new String[0]);
         PlaceholderInt lerpInPlaceholder = this.mlc.getPlaceholderInteger(new String[]{"li", "lerpin"}, (int)(property.getLerpIn() * 20.0), new String[0]);
         PlaceholderInt lerpOutPlaceholder = this.mlc.getPlaceholderInteger(new String[]{"lo", "lerpout"}, (int)(property.getLerpOut() * 20.0), new String[0]);
         BlueprintAnimation.LoopMode loopMode = property.getForceLoopMode();
         PlaceholderString loopModePlaceholder = this.mlc
            .getPlaceholderString(new String[]{"l", "loop"}, loopMode == null ? null : loopMode.name(), new String[0]);
         loopMode = BlueprintAnimation.LoopMode.getOrNull(MythicUtils.getOrNull(loopModePlaceholder, meta, target));
         BlueprintAnimation.OverrideMode override = property.getForceOverride();
         PlaceholderString overridePlaceholder = this.mlc
            .getPlaceholderString(new String[]{"ov", "override"}, override == null ? null : override.name(), new String[0]);
         override = BlueprintAnimation.OverrideMode.getOrNull(MythicUtils.getOrNull(overridePlaceholder, meta, target));
         Easing curveIn = property.getLerpCurveIn();
         PlaceholderString curveInPlaceholder = this.mlc.getPlaceholderString(new String[]{"lci", "lerpcurvein"}, curveIn.name(), new String[0]);
         curveIn = Easing.get(MythicUtils.getOrNull(curveInPlaceholder, meta, target));
         Easing curveOut = property.getLerpCurveOut();
         PlaceholderString curveOutPlaceholder = this.mlc.getPlaceholderString(new String[]{"lco", "lerpcurveout"}, curveOut.name(), new String[0]);
         curveOut = Easing.get(MythicUtils.getOrNull(curveOutPlaceholder, meta, target));
         property.setSpeed(speedPlaceholder.get(meta, target));
         property.setLerpIn(lerpInPlaceholder.get(meta, target) * 0.05);
         property.setLerpOut(lerpOutPlaceholder.get(meta, target) * 0.05);
         property.setForceLoopMode(loopMode);
         property.setForceOverride(override);
         property.setLerpCurveIn(curveIn);
         property.setLerpCurveOut(curveOut);
      }
   }
}
