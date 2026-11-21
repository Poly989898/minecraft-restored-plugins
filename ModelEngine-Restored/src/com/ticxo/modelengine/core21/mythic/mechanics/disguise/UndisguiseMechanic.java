package com.ticxo.modelengine.core21.mythic.mechanics.disguise;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import org.bukkit.entity.Player;

@MythicMechanic(name = "undisguise", aliases = "modelundisguise")
public class UndisguiseMechanic implements ITargetedEntitySkill {
   private final PlaceholderString modelId;

   public UndisguiseMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      if (ServerInfo.scheduleOnRegion(target.getBukkitEntity(), () -> this.castAtEntity(meta, target))) {
         return SkillResult.SUCCESS;
      }

      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      }

      Player player = (Player)target.getBukkitEntity();
      ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(player);
      if (modeledEntity != null) {
         String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
         if (modelId == null) {
            modeledEntity.setBaseEntityVisible(true);
            modeledEntity.markRemoved();
            ModelEngineAPI.getEntityHandler().setForcedInvisible(player, false);
         } else {
            modeledEntity.removeModel(modelId).ifPresent(ActiveModel::destroy);
            if (modeledEntity.getModels().isEmpty()) {
               modeledEntity.setBaseEntityVisible(true);
               modeledEntity.markRemoved();
               ModelEngineAPI.getEntityHandler().setForcedInvisible(player, false);
            }
         }
      }

      return SkillResult.SUCCESS;
   }

   public boolean getTargetsCreatives() {
      return true;
   }
}
