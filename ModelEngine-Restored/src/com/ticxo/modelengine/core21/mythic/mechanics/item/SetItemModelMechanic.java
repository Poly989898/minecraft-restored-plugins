package com.ticxo.modelengine.core21.mythic.mechanics.item;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.types.ItemVariable;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(name = "setitemmodel", aliases = {})
public class SetItemModelMechanic implements ITargetedEntitySkill {
   private final PlaceholderString modelId;
   private final PlaceholderString partId;
   private final PlaceholderString strMat;
   private final PlaceholderInt data;
   private final PlaceholderString color;
   private final boolean enchanted;

   public SetItemModelMechanic(MythicLineConfig config) {
      this.modelId = config.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.partId = config.getPlaceholderString(new String[]{"b", "bone", "p", "pid", "part", "partid"}, null, new String[0]);
      this.strMat = config.getPlaceholderString(new String[]{"i", "item", "mat", "material"}, null, new String[0]);
      this.data = config.getPlaceholderInteger(new String[]{"data"}, 0, new String[0]);
      this.color = config.getPlaceholderString(new String[]{"color"}, null, new String[0]);
      this.enchanted = config.getBoolean(new String[]{"enchant"}, false);
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
            ItemStack stack = BukkitAdapter.adapt(this.getItem(meta, target));
            ((HeldItem)heldItem).setItemProvider(new HeldItem.StaticItemStackSupplier(stack));
         }));
      });
      return SkillResult.SUCCESS;
   }

   private AbstractItemStack getItem(SkillMetadata meta, AbstractEntity target) {
      AbstractItemStack itemStack = null;
      String strMat = MythicUtils.getOrNull(this.strMat, meta, target);

      try {
         return ItemVariable.fromStringWithContext(strMat, PlaceholderContext.ofMeta(meta));
      } catch (Exception e) {
         int data = this.data.get(meta, target);
         String color = MythicUtils.getOrNull(this.color, meta, target);
         boolean isMI = false;

         try {
            isMI = true;
            if (strMat.contains(":")) {
               isMI = false;
               String[] mat = strMat.split(":", 2);
               String mId = mat[0];
               String pId = mat[1];
               ModelBlueprint blueprint = MythicUtils.getBlueprintOrNull(mId);
               if (blueprint == null) {
                  isMI = true;
               } else {
                  BlueprintBone bone = blueprint.getFlatMap().get(pId);
                  if (bone == null) {
                     isMI = true;
                  } else {
                     int mData = bone.getDataId();
                     if (mData == -1) {
                        isMI = true;
                     } else {
                        itemStack = BukkitAdapter.adapt(bone.getBaseItem().getMaterial()).modelData(mData);
                     }
                  }
               }
            }

            if (isMI) {
               Optional<MythicItem> maybeMI = this.getPlugin().getItemManager().getItem(strMat);
               if (maybeMI.isPresent()) {
                  itemStack = maybeMI.get().generateItemStack(1);
               } else {
                  Material mat = Material.valueOf(strMat.toUpperCase());
                  itemStack = BukkitAdapter.adapt(mat).modelData(data);
               }
            }
         } catch (Exception ex) {
            ex.printStackTrace();
            itemStack = BukkitAdapter.adapt(Material.STONE);
         }

         if (itemStack == null) {
            itemStack = BukkitAdapter.adapt(Material.STONE);
         }

         if (!isMI) {
            if (color != null) {
               itemStack.color(color);
            }

            if (this.enchanted) {
               itemStack.enchantmentGlow(true);
            }
         }

         return itemStack;
      }
   }
}
