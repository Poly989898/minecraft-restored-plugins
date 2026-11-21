package com.ticxo.modelengine.core21.mythic.mechanics.mounting;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;

@MythicMechanic(name = "dismountallmodel", aliases = "dismountall")
public class DismountAllMechanic implements ITargetedEntitySkill {
   private final PlaceholderString modelId;
   private final PlaceholderString pBone;

   public DismountAllMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.pBone = mlc.getPlaceholderString(new String[]{"p", "pbone", "seat"}, null, new String[0]);
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      if (ServerInfo.scheduleOnRegion(target.getBukkitEntity(), () -> this.castAtEntity(meta, target))) {
         return SkillResult.SUCCESS;
      }

      ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
      if (model == null) {
         return SkillResult.CONDITION_FAILED;
      }

      String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      if (modelId == null) {
         return SkillResult.INVALID_CONFIG;
      }

      this.dismountLogic(meta, target, modelId);
      return SkillResult.SUCCESS;
   }

   private void dismountLogic(SkillMetadata meta, AbstractEntity target, String modelId) {
      ModelEngineAPI.getModeledEntity(target.getUniqueId())
         .getModel(modelId)
         .ifPresent(activeModel -> activeModel.getMountManager().ifPresent(mountManager -> {
            String parts = MythicUtils.getOrNullLowercase(this.pBone, meta, target);
            if (parts != null) {
               for (String seat : parts.split(",")) {
                  ((MountManager)mountManager).dismountPassengers(seat);
               }
            } else {
               ((MountManager)mountManager).dismountAll();
            }
         }));
   }
}
