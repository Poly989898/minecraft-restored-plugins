package com.ticxo.modelengine.core21.mythic.mechanics.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IPriorityHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import it.unimi.dsi.fastutil.Pair;

@MythicMechanic(name = "defaultstate", aliases = "defaultanimation")
public class DefaultStateMechanic implements ITargetedEntitySkill {
   private final MythicLineConfig config;
   private final PlaceholderString modelId;
   private final PlaceholderString type;
   private ModelState stateType;

   public DefaultStateMechanic(MythicLineConfig mlc) {
      this.config = mlc;
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.type = mlc.getPlaceholderString(new String[]{"t", "type"}, null, new String[0]);
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
      if (model == null) {
         return SkillResult.CONDITION_FAILED;
      }

      String type = MythicUtils.getOrNull(this.type, meta, target);
      if (type == null) {
         return SkillResult.INVALID_CONFIG;
      }

      this.stateType = ModelState.get(type);
      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      MythicUtils.executeOptModelId(model, modelId, activeModel -> this.configureModel(activeModel, meta, target));
      return SkillResult.SUCCESS;
   }

   private void configureModel(ActiveModel activeModel, SkillMetadata meta, AbstractEntity target) {
      AnimationHandler animationHandler = activeModel.getAnimationHandler();
      AnimationHandler.DefaultProperty property = animationHandler.getDefaultProperty(this.stateType);
      PlaceholderString statePlaceholder = this.config.getPlaceholderString(new String[]{"s", "state"}, property.getAnimation(), new String[0]);
      PlaceholderInt lerpInPlaceholder = this.config.getPlaceholderInteger(new String[]{"li", "lerpin"}, (int)(property.getLerpIn() * 20.0), new String[0]);
      PlaceholderInt lerpOutPlaceholder = this.config.getPlaceholderInteger(new String[]{"lo", "lerpout"}, (int)(property.getLerpOut() * 20.0), new String[0]);
      PlaceholderDouble speedPlaceholder = this.config.getPlaceholderDouble(new String[]{"sp", "speed"}, property.getSpeed(), new String[0]);
      PlaceholderBoolean mergePlaceholder = this.config.getPlaceholderBoolean(new String[]{"mg", "merge"}, property.isMerge());
      Pair<String, String> statePair = MiscUtils.getAnimationRef(statePlaceholder.get(meta, target));
      PlaceholderString srcId = this.config
         .getPlaceholderString(new String[]{"src", "sid", "srcmodel", "srcmodelid"}, statePair.first(), new String[0]);
      String state = MiscUtils.createAnimationRef(MythicUtils.getOrNull(srcId, meta, target), statePair.second());
      double lerpIn = lerpInPlaceholder.get(meta, target) * 0.05;
      double lerpOut = lerpOutPlaceholder.get(meta, target) * 0.05;
      double speed = speedPlaceholder.get(meta, target);
      boolean merge = mergePlaceholder.get(meta, target);
      animationHandler.setDefaultProperty(new AnimationHandler.DefaultProperty(this.stateType, state, lerpIn, lerpOut, speed, merge));
      if (animationHandler instanceof IPriorityHandler priorityHandler && priorityHandler.isPlayingAnimation(property.getAnimation())) {
         priorityHandler.stopAnimation(property.getAnimation());
         priorityHandler.playState(this.stateType);
      } else if (animationHandler instanceof IStateMachineHandler stateMachineHandler) {
         stateMachineHandler.refreshState(property);
      }
   }
}
