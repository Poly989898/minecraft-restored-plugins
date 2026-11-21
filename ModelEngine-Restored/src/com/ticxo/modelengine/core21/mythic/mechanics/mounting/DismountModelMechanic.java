package com.ticxo.modelengine.core21.mythic.mechanics.mounting;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Optional;
import org.bukkit.entity.Entity;

@MythicMechanic(name = "dismountmodel", aliases = {})
public class DismountModelMechanic implements ITargetedEntitySkill {
   private final boolean driver;
   private PlaceholderString modelId;
   private PlaceholderString pBone;

   public DismountModelMechanic(MythicLineConfig mlc) {
      this.driver = mlc.getBoolean(new String[]{"d", "drive", "driver"}, true);
      if (!this.driver) {
         this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
         this.pBone = mlc.getPlaceholderString(new String[]{"p", "pbone", "seat"}, null, new String[0]);
      }
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      if (ServerInfo.scheduleOnRegion(target.getBukkitEntity(), () -> this.castAtEntity(meta, target))) {
         return SkillResult.SUCCESS;
      } else {
         ModeledEntity model = ModelEngineAPI.getModeledEntity(meta.getCaster().getEntity().getUniqueId());
         if (model == null) {
            return SkillResult.CONDITION_FAILED;
         } else if (this.driver) {
            ((MountManager)((MountData)model.getMountData()).getMainMountManager()).dismountDriver();
            return SkillResult.SUCCESS;
         } else {
            String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
            model.getModel(modelId).ifPresent(activeModel -> activeModel.getMountManager().ifPresent(mountManager -> {
               Entity entity = target.getBukkitEntity();
               String part = MythicUtils.getOrNullLowercase(this.pBone, meta, target);
               if (part == null) {
                  ((MountManager)mountManager).dismountRider(entity);
               } else {
                  String[] seats = part.split(",");
                  Optional maybeMount = ((MountManager)mountManager).getMount(entity);
                  maybeMount.ifPresent(mount -> {
                     if (this.contains(((BoneBehavior)mount).getBone().getUniqueBoneId(), seats)) {
                        ((MountManager)mountManager).dismountRider(entity);
                     }
                  });
               }
            }));
            return SkillResult.SUCCESS;
         }
      }
   }

   public boolean getTargetsCreatives() {
      return true;
   }

   private boolean contains(String seat, String... seats) {
      for (String s : seats) {
         if (seat.equals(s)) {
            return true;
         }
      }

      return false;
   }
}
