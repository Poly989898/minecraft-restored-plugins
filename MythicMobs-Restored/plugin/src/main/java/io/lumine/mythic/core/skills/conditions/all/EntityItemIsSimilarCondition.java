package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

@MythicCondition(
   author = "Phil",
   name = "entityitemissimilar",
   aliases = {},
   description = "Tests if the item entity is similar to an itemstack"
)
public class EntityItemIsSimilarCondition extends SkillCondition implements IEntityCondition {
   private AbstractItemStack item;

   public EntityItemIsSimilarCondition(String line, MythicLineConfig config) {
      super(line);
      String materialString = config.getString(new String[]{"item", "i", "material", "m", "mm", "mythicitem"}, "DIRT");

      try {
         this.item = getPlugin().getBootstrap().createItem(materialString);
      } catch (IllegalArgumentException e) {
         MythicLogger.errorConditionConfig(this, config, "'" + materialString + "' is not a valid material.");
      }
   }

   @Override
   public boolean check(AbstractEntity target) {
      if (target.getBukkitEntity() instanceof Item entity) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity)) {
            Boolean result = Schedulers.of(entity).supply(() -> this.checkSimilar(entity)).join();
            return result != null ? result : false;
         } else {
            return this.checkSimilar(entity);
         }
      } else {
         return false;
      }
   }

   private boolean checkSimilar(Item entity) {
      ItemStack itemStack = entity.getItemStack();
      return this.item.isSimilar(BukkitAdapter.adapt(itemStack));
   }
}
