package com.ticxo.modelengine.core21.mythic.mechanics.model;

import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
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

@MythicMechanic(name = "statetoggle", aliases = "togglestate")
public class StateToggleMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final PlaceholderString modelId;
   private final PlaceholderString state;
   private final PlaceholderString lerpCurveIn;
   private final PlaceholderString lerpCurveOut;
   private final boolean ignoreLerp;
   private final boolean force;
   private final PlaceholderInt lerpIn;
   private final PlaceholderInt lerpOut;
   private final PlaceholderInt priority;
   private final PlaceholderDouble speed;

   public StateToggleMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.state = mlc.getPlaceholderString(new String[]{"s", "state"}, null, new String[0]);
      this.speed = mlc.getPlaceholderDouble(new String[]{"sp", "speed"}, "1", new String[0]);
      this.lerpIn = mlc.getPlaceholderInteger(new String[]{"li", "lerpin"}, 0, new String[0]);
      this.lerpOut = mlc.getPlaceholderInteger(new String[]{"lo", "lerpout"}, 0, new String[0]);
      this.lerpCurveIn = mlc.getPlaceholderString(new String[]{"lci", "lerpcurvein"}, null, new String[0]);
      this.lerpCurveOut = mlc.getPlaceholderString(new String[]{"lco", "lerpcurveout"}, null, new String[0]);
      this.ignoreLerp = mlc.getBoolean(new String[]{"i", "ignorelerp"}, false);
      this.force = mlc.getBoolean(new String[]{"f", "force"}, true);
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
      String state = MythicUtils.getOrNullLowercase(this.state, meta, target);
      if (state == null) {
         return SkillResult.INVALID_CONFIG;
      }

      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> {
         AnimationHandler handler = activeModel.getAnimationHandler();
         if (handler instanceof IStateMachineHandler stateMachineHandler) {
            if (stateMachineHandler.isPlayingAnimation(state)) {
               this.removeAnimation(stateMachineHandler, state, meta, target);
            } else {
               this.addAnimation(stateMachineHandler, state, meta, target);
            }
         } else if (handler.isPlayingAnimation(state)) {
            this.removeAnimation(handler, state);
         } else {
            this.addAnimation(handler, state, meta, target);
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
      String state = MythicUtils.getOrNullLowercase(this.state, meta);
      if (state == null) {
         return SkillResult.INVALID_CONFIG;
      }

      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> {
         AnimationHandler handler = activeModel.getAnimationHandler();
         if (handler instanceof IStateMachineHandler stateMachineHandler) {
            if (stateMachineHandler.isPlayingAnimation(state)) {
               this.removeAnimation(stateMachineHandler, state, meta, null);
            } else {
               this.addAnimation(stateMachineHandler, state, meta, null);
            }
         } else if (handler.isPlayingAnimation(state)) {
            this.removeAnimation(handler, state);
         } else {
            this.addAnimation(handler, state, meta, null);
         }
      });
      return SkillResult.SUCCESS;
   }

   private void removeAnimation(AnimationHandler handler, String state) {
      if (this.ignoreLerp) {
         handler.forceStopAnimation(state);
      } else {
         handler.stopAnimation(state);
      }
   }

   private void removeAnimation(IStateMachineHandler handler, String state, SkillMetadata meta, AbstractEntity target) {
      int priority = this.priority.get(meta, target);
      if (this.ignoreLerp) {
         handler.forceStopAnimation(priority, state);
      } else {
         handler.stopAnimation(priority, state);
      }
   }

   private void addAnimation(AnimationHandler handler, String state, SkillMetadata meta, AbstractEntity target) {
      int lerpIn = this.lerpIn.get(meta, target);
      int lerpOut = this.lerpOut.get(meta, target);
      double speed = this.speed.get(meta, target);
      this.configProperty(handler.playAnimation(state, lerpIn * 0.05, lerpOut * 0.05, speed, this.force), meta, target);
   }

   private void addAnimation(IStateMachineHandler handler, String state, SkillMetadata meta, AbstractEntity target) {
      int lerpIn = this.lerpIn.get(meta, target);
      int lerpOut = this.lerpOut.get(meta, target);
      double speed = this.speed.get(meta, target);
      int priority = this.priority.get(meta, target);
      this.configProperty(handler.playAnimation(priority, state, lerpIn * 0.05, lerpOut * 0.05, speed, this.force), meta, target);
   }

   private void configProperty(IAnimationProperty property, SkillMetadata meta, AbstractEntity target) {
      if (property != null) {
         Easing curveIn = Easing.getOrNull(MythicUtils.getOrNull(this.lerpCurveIn, meta, target));
         if (curveIn != null) {
            property.setLerpCurveIn(curveIn);
         }

         Easing curveOut = Easing.getOrNull(MythicUtils.getOrNull(this.lerpCurveOut, meta, target));
         if (curveOut != null) {
            property.setLerpCurveOut(curveOut);
         }
      }
   }
}
