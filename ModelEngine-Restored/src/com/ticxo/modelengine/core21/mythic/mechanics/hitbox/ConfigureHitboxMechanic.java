package com.ticxo.modelengine.core21.mythic.mechanics.hitbox;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;

@MythicMechanic(name = "hitboxconfig", aliases = {})
public class ConfigureHitboxMechanic implements ITargetedEntitySkill {
   private final PlaceholderString modelId;
   private final PlaceholderString partId;
   private final PlaceholderDouble shouldPass;

   public ConfigureHitboxMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, null, new String[0]);
      this.shouldPass = mlc.getPlaceholderDouble(new String[]{"pass"}, "1", new String[0]);
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
      if (model == null) {
         return SkillResult.CONDITION_FAILED;
      }

      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      model.getModel(modelId)
         .ifPresent(
            activeModel -> {
               String partId = MythicUtils.getOrNullLowercase(this.partId, meta, target);
               activeModel.getBone(partId)
                  .flatMap(modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.SUB_HITBOX))
                  .ifPresent(subHitbox -> ((SubHitbox)subHitbox).setDamageMultiplier((float)this.shouldPass.get(meta, target)));
            }
         );
      return SkillResult.SUCCESS;
   }
}
