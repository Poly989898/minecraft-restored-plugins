package com.ticxo.modelengine.core21.mythic.mechanics.item;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.drops.EquipSlot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;

@MythicMechanic(name = "linkitembone", aliases = {})
public class LinkItemBoneMechanic implements ITargetedEntitySkill {
   private final PlaceholderString modelId;
   private final PlaceholderString partId;
   private final PlaceholderString slot;

   public LinkItemBoneMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.partId = mlc.getPlaceholderString(new String[]{"b", "bone", "p", "pid", "part", "partid"}, null, new String[0]);
      this.slot = mlc.getPlaceholderString(new String[]{"s", "slot"}, null, new String[0]);
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
      model.getModel(modelId).ifPresent(activeModel -> {
         String partId = MythicUtils.getOrNullLowercase(this.partId, meta, target);
         activeModel.getBone(partId).ifPresent(modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM).ifPresent(heldItem -> {
            String slotString = MythicUtils.getOrNull(this.slot, meta, target);
            if (slotString == null) {
               ((HeldItem)heldItem).clearItemProvider();
            } else {
               EquipmentSlot bukkitSlot = EquipSlot.of(slotString).getBukkitSlot();
               if (bukkitSlot == null) {
                  ((HeldItem)heldItem).clearItemProvider();
               } else {
                  if (target.getBukkitEntity() instanceof LivingEntity livingEntity) {
                     if (livingEntity.getEquipment() == null) {
                        return;
                     }

                     ((HeldItem)heldItem).setItemProvider(new HeldItem.EquipmentSupplier(livingEntity, bukkitSlot));
                  }
               }
            }
         }));
      });
      return SkillResult.SUCCESS;
   }
}
