package com.ticxo.modelengine.core21.mythic.mechanics.entity;

import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import java.util.Optional;
import org.bukkit.entity.Player;

@MythicMechanic(name = "pairmodel", aliases = {})
public class PairModelMechanic implements ITargetedEntitySkill {
   private final boolean hidden;
   private final boolean remove;

   public PairModelMechanic(MythicLineConfig mlc) {
      this.hidden = mlc.getBoolean(new String[]{"h", "hidden"}, false);
      this.remove = mlc.getBoolean(new String[]{"r", "remove"}, false);
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, meta.getCaster().getEntity().getUniqueId());
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      if (model.getBase().getData() instanceof BukkitEntityData data && target.getBukkitEntity() instanceof Player player) {
         if (this.hidden) {
            if (this.remove) {
               data.getTracked().removeForcedHidden(player.getUniqueId());
            } else {
               data.getTracked().addForcedHidden(player.getUniqueId());
            }
         } else if (this.remove) {
            data.getTracked().removeForcedPairing(player.getUniqueId());
         } else {
            data.getTracked().addForcedPairing(player.getUniqueId());
         }
      }

      return SkillResult.SUCCESS;
   }

   public boolean getTargetsCreatives() {
      return true;
   }
}
