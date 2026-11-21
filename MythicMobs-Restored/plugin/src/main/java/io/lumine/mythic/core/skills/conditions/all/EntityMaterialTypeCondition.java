package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.volatilecode.VolatileEntityType;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

@MythicCondition(
   author = "Ashijin",
   name = "entityMaterialType",
   description = "Tests the material type of the target item entity"
)
public class EntityMaterialTypeCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "material",
      aliases = {"mat", "m", "type", "types", "t"},
      description = "A list of material types to match"
   )
   protected EnumSet<Material> materials;

   public EntityMaterialTypeCondition(String line, MythicLineConfig mlc) {
      super(line);
      String entityTypesStringList = mlc.getString(new String[]{"material", "mat", "m", "type", "types", "t"}, null, this.conditionVar);
      Collection<Material> collection = new ArrayList<>();
      if (entityTypesStringList != null) {
         for (String entityTypeString : entityTypesStringList.split(",")) {
            try {
               collection.add(Material.matchMaterial(entityTypeString.toUpperCase()));
            } catch (Exception e) {
               MythicLogger.errorConditionConfig(this, mlc, "Couldn't parse \"" + entityTypeString + "\" as a Material!");
            }
         }
      }

      this.materials = !collection.isEmpty() ? EnumSet.copyOf(collection) : EnumSet.noneOf(Material.class);
   }

   @Override
   public boolean check(AbstractEntity targetEntity) {
      Entity entity = targetEntity.getBukkitEntity();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity)) {
         Boolean result = Schedulers.of(entity).supply(() -> this.checkMaterial(entity)).join();
         return result != null ? result : false;
      } else {
         return this.checkMaterial(entity);
      }
   }

   private boolean checkMaterial(Entity entity) {
      if (entity.getType() != VolatileEntityType.ITEM) {
         return false;
      } else {
         Item item = (Item)entity;
         return this.materials.contains(item.getItemStack().getType());
      }
   }
}
