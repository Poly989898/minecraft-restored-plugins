package io.lumine.mythic.core.skills.conditions.all;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.volatilecode.VolatileEntityType;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

@MythicCondition(
   author = "Ashijin",
   name = "entityItemType",
   description = "Tests the item type of the target item entity"
)
public class EntityItemTypeCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "material",
      aliases = {"mat", "m", "type", "types", "t"},
      description = "A list of item types to match"
   )
   protected List<EntityItemTypeCondition.MatchedItem> materials = Lists.newArrayList();

   public EntityItemTypeCondition(String line, MythicLineConfig mlc) {
      super(line);
      String entityTypesStringList = mlc.getString(new String[]{"material", "mat", "m", "type", "types", "t"}, null, this.conditionVar);
      if (entityTypesStringList != null) {
         for (String entityTypeString : entityTypesStringList.split(",")) {
            try {
               this.materials.add(new EntityItemTypeCondition.MatchedItem(entityTypeString));
            } catch (Exception e) {
               MythicLogger.errorConditionConfig(this, mlc, "Couldn't parse \"" + entityTypeString + "\" as an item!");
            }
         }
      }
   }

   @Override
   public boolean check(AbstractEntity targetEntity) {
      Entity entity = targetEntity.getBukkitEntity();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity)) {
         Boolean result = Schedulers.of(entity).supply(() -> this.checkItemType(entity)).join();
         return result != null ? result : false;
      } else {
         return this.checkItemType(entity);
      }
   }

   private boolean checkItemType(Entity entity) {
      if (entity.getType() != VolatileEntityType.ITEM) {
         return false;
      } else {
         Item item = (Item)entity;

         for (EntityItemTypeCondition.MatchedItem mi : this.materials) {
            if (mi.check(item)) {
               return true;
            }
         }

         return false;
      }
   }

   @Getter
   @ToString
   private class MatchedItem {
      private final Material material;
      private final int model;

      public MatchedItem(String itemString) {
         if (itemString.contains(":")) {
            String[] split = itemString.split(":");
            this.material = Material.matchMaterial(split[0]);
            this.model = Numbers.parseInteger(split[1]).orElse(0);
         } else {
            this.material = Material.matchMaterial(itemString);
            this.model = 0;
         }
      }

      public boolean check(Item itemEntity) {
         if (this.model == 0) {
            return this.material == itemEntity.getItemStack().getType();
         } else {
            ItemStack stack = itemEntity.getItemStack();
            if (!stack.hasItemMeta()) {
               return false;
            } else {
               return this.material != stack.getType() ? false : stack.getItemMeta().getCustomModelData() == this.model;
            }
         }
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof EntityItemTypeCondition.MatchedItem other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.getModel() != other.getModel()) {
            return false;
         } else {
            Object this$material = this.getMaterial();
            Object other$material = other.getMaterial();
            return this$material == null ? other$material == null : this$material.equals(other$material);
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof EntityItemTypeCondition.MatchedItem;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + this.getModel();
         Object $material = this.getMaterial();
         return result * 59 + ($material == null ? 43 : $material.hashCode());
      }
   }
}
